package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @Override
    public List<User> get() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values().parallelStream().collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        validate(user);
        String name = user.getName();
        String login = user.getLogin();
        if (name == null || name.isEmpty()) {
            user.setName(login);
            log.debug("Для пользователя с логином {} установлено новое имя {}", login, user.getName());
        }
        user.setId(++this.id);
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        users.put(user.getId(), user);
        log.debug("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        validate(user);
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            log.debug("Не найден пользователь в списке с id: {}", userId);
            throw new NotFoundException();
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        users.put(userId, user);
        log.debug("Обновлены данные пользователя с id {}. Новые данные: {}", userId, user);
        return user;
    }

    @Override
    public User getUserId(Integer userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public List<User>  addFriends(Integer userId, Integer friendId) {
        User user = getUserId(userId);
        User friend = getUserId(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        update(user);
        update(friend);
        Set<Integer> friendsId = user.getFriends().keySet();
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(getUserId(id));
        }
        return friends;
    }

    @Override
    public void deleteFriends(Integer userId, Integer friendId) {
        User user = getUserId(userId);
        user.deleteFriends(friendId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        User user = getUserId(userId);
        Set<Integer> friendsId = user.getFriends().keySet();
        for (Integer friendId : friendsId) {
            friends.add(getUserId(friendId));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        List<User> friends = new ArrayList<>();
        User user = getUserId(userId);
        Set<Integer> userFriendsId = user.getFriends().keySet();
        User friend = getUserId(friendId);
        Set<Integer> friendsId = friend.getFriends().keySet();
        List<Integer> commonId = userFriendsId.stream().filter(friendsId::contains).collect(Collectors.toList());
        for (Integer id : commonId) {
            friends.add(getUserId(id));
        }
        return friends;
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