package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    public static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public static final int MAX_NAME_SIZE = 200;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> get() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT films.ID, films.NAME, films.DESCRIPTION, films.RELEASE_DATE, films.DURATION, " +
            "films.RATING_ID, ratings.NAME rating_name FROM films LEFT JOIN RATINGS ON films.RATING_ID = ratings.ID";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql);
        while (filmRows.next()) {
            Film film = makeFilm(filmRows);
            films.add(film);
            log.info("В список запроса получения всех фильмов добавлен фильм: {}", film);
        }
        log.info("Количество фильмов в базе: {}", films.size());
        return films;
    }

    private Film makeFilm(SqlRowSet filmRows) {
        int filmId = filmRows.getInt("id");
        String releaseDate = filmRows.getString("release_date");
        Rating filmRating = Rating.builder()
                .id(filmRows.getInt("rating_id"))
                .name(filmRows.getString("RATING_NAME"))
                .build();
        Film film = Film.builder()
                .id(filmId)
                .name(filmRows.getString("name"))
                .description(filmRows.getString("description"))
                .releaseDate(LocalDate.parse(releaseDate))
                .duration(filmRows.getLong("duration"))
                .likesByUsers(getLikes(filmId))
                .genres(getFilmGenre(filmId))
                .mpa(filmRating)
                .build();
        return film;
    }

    private Set<Genre> getFilmGenre(int filmId) {
        Set<Genre> filmGenre = new HashSet<>();
        String sql = "select * from genres where id in (select genre_id from film_genres where film_id = ?)";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, filmId);
        while (genreRows.next()) {
            filmGenre.add(makeGenre(genreRows));
        }
        return filmGenre;
    }

    private Genre makeGenre(SqlRowSet genreRows) {
        Genre genre = Genre.builder()
                .id(genreRows.getInt("id"))
                .name(genreRows.getString("name"))
                .build();
        return genre;
    }

    private Set<Integer> getLikes(int filmId) {
        Set<Integer> userLikes = new HashSet<>();
        String sql = "select user_id from likes where film_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, filmId);
        while (likesRows.next()) {
            userLikes.add(likesRows.getInt("user_id"));
        }
        return userLikes;
    }

    @Override
    public Film create(Film film) {
        validate(film);
        if (film.getLikesByUsers() == null) {
            film.setLikesByUsers(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        String sqlQuery = "insert into films (name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setString(3, film.getReleaseDate().toString());
                stmt.setLong(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        addFilmGenres(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validate(film);
        if (film.getLikesByUsers() == null) {
            film.setLikesByUsers(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        int filmId = film.getId();
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where id = ?";
        int totalUpdate = jdbcTemplate.update(sqlQuery,
                 film.getName(),
                 film.getDescription(),
                 film.getReleaseDate(),
                 film.getDuration(),
                 film.getMpa().getId(),
                 filmId);
        if (totalUpdate == 0) {
            throw new NotFoundException();
        }
        sqlQuery = "delete from likes where film_id = ? ";
        jdbcTemplate.update(sqlQuery, filmId);
        addFilmLikes(film);
        sqlQuery = "delete from film_genres where film_id = ? ";
        jdbcTemplate.update(sqlQuery, filmId);
        addFilmGenres(film);
        log.info("Обновлено записей: {}", totalUpdate);
        return getFilmById(filmId);
    }

    private void addFilmGenres(Film film) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) " +
                "values (?, ?)";
        int filmId = film.getId();
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            int genreId = genre.getId();
            int totalUpdate = jdbcTemplate.update(sqlQuery,
                    filmId,
                    genreId);
            if (totalUpdate > 0) {
                log.info("Фильму с id {} добавлен жанр с id {}", filmId, genreId);
            }
        }
    }

    private void addFilmLikes(Film film) {
        String sqlQuery = "insert into likes (film_id, user_id) " +
                "values (?, ?)";
        int filmId = film.getId();
        Set<Integer> likesByUsers = film.getLikesByUsers();
        for (Integer userId : likesByUsers) {
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    userId);
        }
    }

    @Override
    public Film getFilmById(Integer filmId) {
        String sql = "SELECT films.ID, films.NAME, films.DESCRIPTION, films.RELEASE_DATE, films.DURATION, " +
                "films.RATING_ID, ratings.NAME rating_name FROM films LEFT JOIN RATINGS " +
                "ON films.RATING_ID = ratings.ID where films.ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            log.info("Найден фильм в базе: {}", film);
            return film;
        } else {
            log.info("В списке отсутствует фильм с id: {}", filmId);
            throw new NotFoundException();
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        film.addLike(userId);
        update(film);
        log.info("Добавлен like от пользователя c id {} для фильма: {}", userId, film);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        film.deleteLike(userId);
        update(film);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT films.ID, films.NAME, films.DESCRIPTION, films.RELEASE_DATE, films.DURATION," +
                "films.RATING_ID, ratings.NAME rating_name FROM films " +
                "LEFT JOIN RATINGS ON films.RATING_ID = ratings.ID LEFT JOIN " +
                "(SELECT film_id, count(user_id) top FROM LIKES GROUP BY film_id) top_films " +
                "ON films.ID = top_films.film_id ORDER BY IFNULL(top_films.TOP, 0) DESC LIMIT ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, count);
        while (filmRows.next()) {
            films.add(makeFilm(filmRows));
        }
        log.info("Количество популярных фильмов: {}", films.size());
        return films;
    }

    public static void validate(Film film) {
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        long duration = film.getDuration();
        if (name == null || name.isEmpty()) {
            throw new ValidationException("Film name invalid");
        }
        if (description == null || description.length() > MAX_NAME_SIZE) {
            throw new ValidationException("Film description invalid");
        }
        if (film.getReleaseDate() == null || releaseDate.isBefore(FILM_BIRTHDAY)) {
            throw new ValidationException("Film releaseDate invalid");
        }
        if (duration <= 0) {
            throw new ValidationException("Film duration invalid");
        }
    }
}
