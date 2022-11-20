package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User saveUser(User user) {
        setUserNameAsLoginIfEmpty(user);
        return userStorage.saveUser(user);
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User updateUser(User user) {
        findUserById(user.getId());
        setUserNameAsLoginIfEmpty(user);
        return userStorage.updateUser(user);
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        return userStorage.findCommonFriends(id, otherId);
    }

    public List<User> findUserFriends(Long id) {
        return userStorage.findUserFriends(id);
    }

    public boolean saveFriend(Long id, Long friendId) {
        findUserById(id);
        findUserById(friendId);
        return userStorage.saveFriend(id, friendId);
    }

    public boolean deleteFriend(Long id, Long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    private void setUserNameAsLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
