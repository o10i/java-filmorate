package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage)  {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (userStorage.getUsers().containsKey(user.getId())) {
            throw new UserAlreadyExistException("Пользователь с id " + user.getId() + " уже зарегистрирован.");
        }
        setUserNameAsLoginIfEmpty(user);
        User createdUser = userStorage.create(user);
        log.debug("Пользователь с id {} зарегистрирован.", user.getId());
        return createdUser;
    }

    public User update(User user) {
        if (!userStorage.getUsers().containsKey(user.getId())) {
            log.debug("Пользователь с id {} не зарегистрирован.", user.getId());
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не зарегистрирован.");
        }
        setUserNameAsLoginIfEmpty(user);
        User updatedUser = userStorage.update(user);
        log.debug("Пользователь с id {} обновлён.", user.getId());
        return updatedUser;
    }

    public List<User> getAll() {
        log.debug("Все пользователи возвращены.");
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.debug("Пользователь с id {} не зарегистрирован.", id);
            throw new UserNotFoundException("Пользователь с id " + id+ " не зарегистрирован.");
        }
        log.debug("Пользователь с id {} возвращён.", id);
        return userStorage.getUsers().get(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> userFriends = getUserFriends(id);
        List<User> otherUserFriends = getUserFriends(otherId);
        log.debug("Общие друзья пользователей с id {} и {} возвращены.", id, otherId);
        return userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());
    }

    public List<User> getUserFriends(Long id) {
        Set<Long> friendsId = getUserById(id).getFriends();
        List<User> collect = new ArrayList<>();
        if (friendsId != null) {
            collect = friendsId.stream().map(this::getUserById).collect(Collectors.toList());
        }
        log.debug("Все друзья пользователя с id {} возвращены.", id);
        return collect;
    }

    public void addFriend(Long id, Long friendId) {
        User userId = getUserById(id);
        if (userId.getFriends() == null) {
            userId.setFriends(new HashSet<>());
        }
        User userFriendId = getUserById(friendId);
        if (userFriendId.getFriends() == null) {
            userFriendId.setFriends(new HashSet<>());
        }
        userId.getFriends().add(friendId);
        userFriendId.getFriends().add(id);
        log.debug("Пользователи с id {} и {} добавились друг другу в друзья.", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        getUserById(id).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(id);
        log.debug("Пользователи с id {} и {} удалили друг друга из друзей.", id, friendId);
    }

    private void setUserNameAsLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю без имени присвоено новое имя {}.", user.getLogin());
        }
    }
}
