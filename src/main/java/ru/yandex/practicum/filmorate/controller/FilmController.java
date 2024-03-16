package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    public static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public static final int MAX_NAME_SIZE = 200;

    @GetMapping("/films")
    public List<Film> get() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values().parallelStream().collect(Collectors.toList());
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(++this.id);
        films.put(film.getId(), film);
        log.debug("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        validate(film);
        int filmId = film.getId();
        if (!films.containsKey(filmId)) {
            throw new ValidationException("Не найден фильм в списке с id: {}"+filmId);
        }
        films.put(filmId, film);
        log.debug("Обновлены данные фильма с id {}. Новые данные: {}", filmId, film);
        return film;
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
