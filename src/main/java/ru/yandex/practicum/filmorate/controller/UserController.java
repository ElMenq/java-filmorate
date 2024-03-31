package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @ResponseBody
    public List<User> get() {
        return userService.get();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user {}", user);
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        log.info("User data updated {}", user);
        return userService.update(user);
    }

    @GetMapping("/users/{id}")
    public User getUserId(@PathVariable("id") Integer userId) {
        log.info("Get user by id {}", userId);
        return userService.getUserId(userId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public List<User> addFriends(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        log.info("Add friend by id {}", friendId);
        return userService.addFriends(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        log.info("Delete friend by id {}", friendId);
        userService.deleteFriends(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        log.info("Get friend by id {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer friendId) {
        log.info("Get common friends by id {}", friendId);
        return userService.getCommonFriends(userId, friendId);
    }
}
