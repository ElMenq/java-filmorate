package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> get() {
        return filmService.get();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("Creating film {}", film);
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Film data updated {}", film);
        return filmService.update(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilmId(@PathVariable("id") Integer filmId) {
        log.info("Get film by id {}", filmId);
        return filmService.getFilmId(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        log.info("Add like film id={} from user={}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        log.info("Delete like film id={} from user={}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Get top films count=" + count);
        return filmService.getTopFilms(count);
    }
}
