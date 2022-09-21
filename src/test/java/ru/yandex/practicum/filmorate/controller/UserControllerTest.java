package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceprion.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    UserController uc = new UserController();

    @Test
    void create() {
        User user = User.builder()
                .login("Test")
                .name("create")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.create(user);
        User savedUser = uc.getAll().get(0);
        assertEquals(1, uc.getAll().size(), "Количество пользователей не совпадает.");
        assertEquals(user, savedUser, "Пользователь не зарегестрирован.");
    }

    @Test
    void createWithFailedLogin() {
        User user = User.builder()
                .login("Test test")
                .name("createWithFailedLogin")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> uc.create(user), "Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void createWithFailedEmail() {
        User user = User.builder()
                .login("Test")
                .name("createWithFailedEmail")
                .email("mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> uc.create(user), "Электронная почта не может быть пустой и должна содержать символ '@'.");
    }

    @Test
    void createWithFailedBirthday() {
        User user = User.builder()
                .login("Test")
                .name("createWithFailedEmail")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2500, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> uc.create(user), "Дата рождения не может быть в будущем.");
    }

    @Test
    void createWithEmptyName() {
        User user = User.builder()
                .login("createWithEmptyName")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.create(user);
        User savedUser = uc.getAll().get(0);
        assertEquals(1, uc.getAll().size(), "Количество пользователей не совпадает.");
        assertEquals("createWithEmptyName", savedUser.getName(), "Пользователю не присвоено имя в качестве логина.");
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

    @Test
    void getUsers() {
        User user1 = User.builder()
                .login("Test1")
                .name("getUsers")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        User user2 = User.builder()
                .login("Test2")
                .name("getUsers")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        uc.create(user1);
        uc.create(user2);
        assertEquals(2, uc.getAll().size(), "Количество пользователей не совпадает.");
    }
}