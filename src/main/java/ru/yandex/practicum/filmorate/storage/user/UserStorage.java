package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> get();

    User create(User user);

    User update(User user);

    User getUserId(Integer userId);

    List<User> addFriends(Integer userId, Integer friendId);

    void deleteFriends(Integer userId, Integer friendId);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer friendId);
}
