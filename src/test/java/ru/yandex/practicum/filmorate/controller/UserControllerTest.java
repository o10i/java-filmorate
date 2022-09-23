package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController uc = new UserController();

    @Test
    void createWithEmptyName() {
        User user = User.builder()
                .login("Test")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.create(user);
        User savedUser = uc.getAll().get(0);
        assertEquals(1, uc.getAll().size(), "Количество пользователей не совпадает.");
        assertEquals(savedUser.getName(), savedUser.getLogin(), "Пользователь не зарегистрирован.");
    }

    @Test
    void update() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.create(user);
        User updatedUser = User.builder()
                .id(1)
                .login("Test")
                .name("update")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.update(updatedUser);
        User savedUser = uc.getAll().get(0);
        assertEquals(1, uc.getAll().size(), "Количество пользователей не совпадает.");
        assertEquals(updatedUser, savedUser, "Пользователь не обновился.");
    }

    @Test
    void updateWithUnknownId() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.create(user);
        User updatedUser = User.builder()
                .id(0)
                .login("Test")
                .name("updateWithUnknownId")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> uc.update(updatedUser), "Пользователя с id " + updatedUser.getId() + " не существует.");
    }
}