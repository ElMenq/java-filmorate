package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @GetMapping("/users")
    public List<User> get() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values().parallelStream().collect(Collectors.toList());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        validate(user);
        String name = user.getName();
        String login = user.getLogin();
        if (name == null || name.isEmpty()) {
            user.setName(login);
            log.debug("Для пользователя с логином {} установлено новое имя {}", login, user.getName());
        }
        user.setId(++this.id);
        users.put(user.getId(), user);
        log.debug("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        validate(user);
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new ValidationException("Не найден пользователь в списке с id:");
        }
        users.put(userId, user);
        log.debug("Обновлены данные пользователя с id {}. Новые данные: {}", userId, user);
        return user;
    }

    public static void validate(User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();
        if (email == null || email.isEmpty() || !email.contains("@")) {
            log.debug("User email invalid");
            throw new ValidationException("User email invalid");
        }
        if (login == null || login.isEmpty() || login.contains(" ")) {
            throw new ValidationException("User login invalid");
        }
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday invalid");
        }
    }
}
