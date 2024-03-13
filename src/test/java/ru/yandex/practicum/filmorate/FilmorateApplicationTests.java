package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.*;
import ru.yandex.practicum.filmorate.model.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void validateFilm() {
		Film film = Film.builder()
				.name("Дневник памяти")
				.description("Это история отношений юноши и девушки из разных социальных слоев, живших в Южной Каролине."+
						" Ной и Элли провели вместе незабываемое лето, пока их не разделили вначале родители, а затем Вторая мировая война.")
				.releaseDate(LocalDate.of(1895, 12, 19))
				.duration(0)
				.build();
		assertEquals(true, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidName() {
		Film film = Film.builder()
				.description("Это история отношений юноши и девушки из разных социальных слоев, живших в Южной Каролине."+
						" Ной и Элли провели вместе незабываемое лето, пока их не разделили вначале родители, а затем Вторая мировая война.")
				.releaseDate(LocalDate.of(1896, 12, 19))
				.duration(90)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidDescription() {
		Film film = Film.builder()
				.name("Дневник памяти")
				.description("Это история отношений юноши и девушки из разных социальных слоев, живших в Южной Каролине."+
						" Ной и Элли провели вместе незабываемое лето, пока их не разделили вначале родители, а затем Вторая мировая война.")
				.releaseDate(LocalDate.of(1896, 12, 19))
				.duration(-90)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidReleaseDate() {
		Film film = Film.builder()
				.name("Дневник памяти")
				.description("Это история отношений юноши и девушки из разных социальных слоев, живших в Южной Каролине."+
						" Ной и Элли провели вместе незабываемое лето, пока их не разделили вначале родители, а затем Вторая мировая война.")
				.releaseDate(LocalDate.of(1890, 12, 28))
				.duration(90)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateFilmInvalidDuration() {
		Film film = Film.builder()
				.name("Дневник памяти")
				.description("Это история отношений юноши и девушки из разных социальных слоев, живших в Южной Каролине."+
						" Ной и Элли провели вместе незабываемое лето, пока их не разделили вначале родители, а затем Вторая мировая война.")
				.releaseDate(LocalDate.of(1896, 12, 18))
				.duration(-90)
				.build();
		assertEquals(false, FilmController.validate(film));
	}

	@Test
	void validateUser() {
		User user = User.builder()
				.email("email@email.com")
				.login("login")
				.birthday(LocalDate.of(2020, 01, 17))
				.build();
		assertEquals(true, UserController.validate(user));
	}

	@Test
	void validateUserInvalidEmail() {
		User user = User.builder()
				.email("email@email.com")
				.login("login")
				.birthday(LocalDate.of(2021, 12, 12))
				.build();
		assertEquals(false, UserController.validate(user));
	}

	@Test
	void validateUserInvalidLogin() {
		User user = User.builder()
				.email("email@email.com")
				.login("login")
				.birthday(LocalDate.of(2023, 12, 11))
				.build();
		assertEquals(false, UserController.validate(user));
	}

	@Test
	void validateUserInvalidBirthday() {
		User user = User.builder()
				.email("email@email.com")
				.login("login")
				.birthday(LocalDate.of(2024, 12, 9))
				.build();
		assertEquals(false, UserController.validate(user));
	}
}
