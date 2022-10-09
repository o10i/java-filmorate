package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        validateUserId(userStorage.getUsers().containsKey(user.getId()), user, " уже зарегистрирован.");
        setUserNameAsLoginIfEmpty(user);
        User createdUser = userStorage.create(user);
        log.debug("Пользователь с id {} зарегистрирован", user.getId());
        return createdUser;
    }

    public User update(User user) {
        validateUserId(!userStorage.getUsers().containsKey(user.getId()), user, " не зарегистрирован.");
        setUserNameAsLoginIfEmpty(user);
        User updatedUser = userStorage.update(user);
        log.info("Пользователь с id {} обновлён", user.getId());
        return updatedUser;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        return userStorage.getUsers().get(id);
    }

    public void addFriend(Long id, Long friendId) {
        getUserById(id).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(id);
    }

    public void removeFriend(Long id, Long friendId) {
        getUserById(id).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(id);
    }

    public List<User> getUserFriends(Long id) {
        Set<Long> friendsId = getUserById(id).getFriends();
        return friendsId.stream().map(this::getUserById).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        List<User> userFriends = getUserFriends(id);
        List<User> otherUserFriends = getUserFriends(otherId);
        return userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());
    }

    private void validateUserId(boolean users, User user, String x) {
        if (users) {
            throw new ValidationException("Пользователь с id " + user.getId() + x);
        }
    }

    private void setUserNameAsLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователю с id {} присвоено новое имя {}", user.getId(), user.getLogin());
        }
    }
}
