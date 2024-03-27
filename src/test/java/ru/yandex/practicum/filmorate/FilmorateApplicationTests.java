package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

	private static InMemoryFilmStorage inMemoryFilmStorage;
	private static InMemoryUserStorage inMemoryUserStorage;

	@Test
	void validateFilmFail() {
		final Film film = new Film();
		Exception exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.validate(film));
		assertEquals("Film name invalid", exception.getMessage());

		film.setName("filmName");
		exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.validate(film));
		assertEquals("Film description invalid", exception.getMessage());

		film.setDescription("filmDescription");
		exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.validate(film));
		assertEquals("Film releaseDate invalid", exception.getMessage());

		film.setReleaseDate(LocalDate.of(1895, 12, 28));
		exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.validate(film));
		assertEquals("Film duration invalid", exception.getMessage());

		film.setDuration(200);
	}

	@Test
	void validateFilmOk() {
		final Film validFilms = new Film();
		validFilms.setName("filmName");
		validFilms.setDescription("filmDescription");
		validFilms.setReleaseDate(LocalDate.of(1895, 12, 28));
		validFilms.setDuration(200);
		inMemoryFilmStorage.validate(validFilms);
	}

	@Test
	void validateUserFail() {
		final User user = new User();
		Exception exception = assertThrows(ValidationException.class, () -> inMemoryUserStorage.validate(user));
		assertEquals("User email invalid", exception.getMessage());

		user.setLogin("userLogin");
		exception = assertThrows(ValidationException.class, () -> inMemoryUserStorage.validate(user));
		assertEquals("User email invalid", exception.getMessage());

		user.setEmail("user email");
		exception = assertThrows(ValidationException.class, () -> inMemoryUserStorage.validate(user));
		assertEquals("User email invalid", exception.getMessage());

		user.setEmail("user@email");
		user.setBirthday(LocalDate.MAX);
		exception = assertThrows(ValidationException.class, () -> inMemoryUserStorage.validate(user));
		assertEquals("User birthday invalid", exception.getMessage());
	}

	@Test
	void validateUserOk() {
		final User validUser = new User();
		validUser.setName("Used Name");
		validUser.setLogin("userLogin");
		validUser.setEmail("user@email");
		validUser.setBirthday(LocalDate.of(2000, 1, 1));
		inMemoryUserStorage.validate(validUser);
	}
}
