package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User saveUser(User user);

    User findUserById(Long id);

    List<User> findAllUsers();

    User updateUser(User user);

    boolean deleteUser(Long id);
}
