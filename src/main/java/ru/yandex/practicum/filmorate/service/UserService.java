package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User saveUser(User user) {
        setUserNameAsLoginIfEmpty(user);
        User savedUser = userStorage.saveUser(user);
        log.debug("Пользователь {} с id={} зарегистрирован.", user.getName(), user.getId());
        return savedUser;
    }

    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }
        log.debug("Пользователь с id={} найден.", id);
        return user;
    }

    public List<User> findAllUsers() {
        List<User> users = userStorage.findAllUsers();
        log.debug("Все пользователи найдены.");
        return users;
    }

    public User updateUser(User user) {
        findUserById(user.getId());
        setUserNameAsLoginIfEmpty(user);
        User updatedUser = userStorage.updateUser(user);
        log.debug("Пользователь {} с id={} обновлён.", user.getName(), user.getId());
        return updatedUser;
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = userStorage.findCommonFriends(id, otherId);
        log.debug("Общие друзья пользователей с id={} и id={} найдены.", id, otherId);
        return commonFriends;
    }

    public List<User> findUserFriends(Long id) {
        List<User> userFriends = userStorage.findUserFriends(id);
        log.debug("Все друзья пользователя с id={} найдены.", id);
        return userFriends;
    }

    public boolean saveFriend(Long id, Long friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean result = userStorage.saveFriend(id, friendId);
        log.debug("У пользователя с id={} новый друг с id={}.", id, friendId);
        return result;
    }

    public boolean deleteFriend(Long id, Long friendId) {
        boolean result = userStorage.deleteFriend(id, friendId);
        log.debug("Пользователь с id={} удалил друга c id={}.", id, friendId);
        return result;
    }

    private void setUserNameAsLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю без имени присвоено новое имя '{}'.", user.getLogin());
        }
    }
}
