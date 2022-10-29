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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User saveUser(User user) {
        if (userStorage.findUserById(user.getId()) != null) {
            throw new UserAlreadyExistException("Пользователь с id " + user.getId() + " уже зарегистрирован.");
        }
        setUserNameAsLoginIfEmpty(user);
        User savedUser = userStorage.saveUser(user);
        log.debug("Пользователь с id {} зарегистрирован.", user.getId());
        return savedUser;
    }

    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не зарегистрирован.");
        }
        log.debug("Пользователь с id {} найден.", id);
        return user;
    }

    public List<User> findAllUsers() {
        log.debug("Все пользователи найдены.");
        return userStorage.findAllUsers();
    }

    public User updateUser(User user) {
        if (userStorage.findUserById(user.getId()) == null) {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не зарегистрирован.");
        }
        setUserNameAsLoginIfEmpty(user);
        User updatedUser = userStorage.updateUser(user);
        log.debug("Пользователь с id {} обновлён.", user.getId());
        return updatedUser;
    }

    public boolean deleteUser(Long id) {
        if (userStorage.findUserById(id) == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не зарегистрирован.");
        }
        log.debug("Пользователь с id {} удалён.", id);
        return true;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> userFriends = getUserFriends(id);
        List<User> otherUserFriends = getUserFriends(otherId);
        log.debug("Общие друзья пользователей с id {} и {} возвращены.", id, otherId);
        return userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());
    }

    public List<User> getUserFriends(Long id) {
        Set<Long> friendsId = findUserById(id).getFriends();
        List<User> collect = new ArrayList<>();
        if (friendsId != null) {
            collect = friendsId.stream().map(this::findUserById).collect(Collectors.toList());
        }
        log.debug("Все друзья пользователя с id {} возвращены.", id);
        return collect;
    }

    public void addFriend(Long id, Long friendId) {
        User userId = findUserById(id);
        if (userId.getFriends() == null) {
            userId.setFriends(new HashSet<>());
        }
        User userFriendId = findUserById(friendId);
        if (userFriendId.getFriends() == null) {
            userFriendId.setFriends(new HashSet<>());
        }
        userId.getFriends().add(friendId);
        userFriendId.getFriends().add(id);
        log.debug("Пользователи с id {} и {} добавились друг другу в друзья.", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        findUserById(id).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(id);
        log.debug("Пользователи с id {} и {} удалили друг друга из друзей.", id, friendId);
    }

    private void setUserNameAsLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю без имени присвоено новое имя {}.", user.getLogin());
        }
    }
}
