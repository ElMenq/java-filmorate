package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> get() {
        return userStorage.get();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserId(userId);
    }

    public List<User> addFriends(Integer userId, Integer friendId) {
        return userStorage.addFriends(userId, friendId);
    }

    public void deleteFriends(Integer userId, Integer friendId) {
        userStorage.deleteFriends(userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        final User user = userStorage.getUserId(userId);
        return userStorage.getFriends(user.getId());
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }
}
