package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    private User getUser() {
        return User.builder().email("test@mail.ru").login("testLogin").name("testName").birthday(Date.valueOf("1946-08-20").toLocalDate()).friends(new ArrayList<>()).build();
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1");

    }

    @Test
    void testSaveUser() {
        User user = getUser();
        User userSaved = userDbStorage.saveUser(user);
        user.setId(1L);
        assertEquals(user, userSaved);
    }

    @Test
    void testSaveUserWithLoginSpace() {
        User user = getUser();
        user.setLogin("test test");
        user.setLogin("test test");
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.saveUser(user), "Логин должен быть без пробелов.");
    }

    @Test
    void testSaveUserWithEmptyLogin() {
        User user = getUser();
        user.setLogin("");
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.saveUser(user), "Логин не должен быть пустым.");
    }

    @Test
    void testSaveUserWithFutureBirthday() {
        User user = getUser();
        user.setBirthday(Date.valueOf("2122-11-03").toLocalDate());
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.saveUser(user), "День рождения не может быть в будущем.");
    }

    @Test
    void testSaveUserWithEmptyEmail() {
        User user = getUser();
        user.setEmail("");
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.saveUser(user), "Электронная почта не может быть пустой.");
    }

    @Test
    void testSaveUserWithoutAtInEmail() {
        User user = getUser();
        user.setEmail("testmail.ru");
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.saveUser(user), "Электронная почта должна содержать символ '@'.");
    }

    @Test
    void testUpdateUser() {
        User user = userDbStorage.saveUser(getUser());
        user.setLogin("testUpdateLogin");
        assertEquals(user, userDbStorage.updateUser(user));
    }

    @Test
    void testUpdateUnknownUser() {
        User user = getUser();
        user.setId(9999L);
        assertThrows(ObjectNotFoundException.class, () -> userDbStorage.updateUser(user), "Пользователь с id " + user.getId() + " не найден.");
    }

    @Test
    void testFindAllUsers() {
        User user = userDbStorage.saveUser(getUser());
        List<User> users = List.of(user);
        assertEquals(users, userDbStorage.findAllUsers());
    }

    @Test
    void testFindUserById() {
        User user = userDbStorage.saveUser(getUser());
        User user2 = userDbStorage.saveUser(getUser());
        User user3 = userDbStorage.saveUser(getUser());
        assertEquals(user, userDbStorage.findUserById(1L));
        assertEquals(user2, userDbStorage.findUserById(2L));
        assertEquals(user3, userDbStorage.findUserById(3L));
    }

    @Test
    void testFindUnknownUser() {
        assertThrows(ObjectNotFoundException.class, () -> userDbStorage.findUserById(9999L), "Пользователь с id " + 9999 + " не найден.");
    }

    @Test
    void testSaveOneUserFriend() {
        userDbStorage.saveUser(getUser());
        userDbStorage.saveUser(getUser());
        assertTrue(userDbStorage.saveFriend(1L, 2L));
    }

    @Test
    void testSaveUnknownUserFriend() {
        userDbStorage.saveUser(getUser());
        assertThrows(ObjectNotFoundException.class, () -> userDbStorage.saveFriend(1L, -1L), "Пользователь с id " + -1L + " не найден.");
    }

    @Test
    void testFindOneUserFriend() {
        userDbStorage.saveUser(getUser());
        User user = userDbStorage.saveUser(getUser());
        userDbStorage.saveFriend(1L, 2L);
        assertEquals(List.of(user), userDbStorage.findUserFriends(1L));
    }

    @Test
    void testFindEmptyFriendsOfFriend() {
        userDbStorage.saveUser(getUser());
        userDbStorage.saveUser(getUser());
        userDbStorage.saveFriend(1L, 2L);
        assertEquals(new ArrayList<>(), userDbStorage.findUserFriends(2L));
    }

    @Test
    void testFindTwoUserFriends() {
        userDbStorage.saveUser(getUser());
        User user = userDbStorage.saveUser(getUser());
        User user2 = userDbStorage.saveUser(getUser());
        userDbStorage.saveFriend(1L, 2L);
        userDbStorage.saveFriend(1L, 3L);
        assertEquals(List.of(user, user2), userDbStorage.findUserFriends(1L));
    }

    @Test
    void testFindEmptyCommonFriends() {
        assertEquals(new ArrayList<>(), userDbStorage.findCommonFriends(1L, 2L));
    }

    @Test
    void testFindOneCommonFriend() {
        userDbStorage.saveUser(getUser());
        userDbStorage.saveUser(getUser());
        User user = userDbStorage.saveUser(getUser());
        userDbStorage.saveFriend(1L, 2L);
        userDbStorage.saveFriend(1L, 3L);
        userDbStorage.saveFriend(2L, 3L);
        assertEquals(List.of(user), userDbStorage.findCommonFriends(1L, 2L));
    }

    @Test
    void testDeleteUserFriend() {
        userDbStorage.saveUser(getUser());
        userDbStorage.saveUser(getUser());
        userDbStorage.saveFriend(1L, 2L);
        assertTrue(userDbStorage.deleteFriend(1L, 2L));
    }

    @Test
    void testFindOneCommonFriendAfterDeletingOfFriend() {
        userDbStorage.saveUser(getUser());
        userDbStorage.saveUser(getUser());
        User user = userDbStorage.saveUser(getUser());
        userDbStorage.saveFriend(1L, 2L);
        userDbStorage.saveFriend(1L, 3L);
        userDbStorage.saveFriend(2L, 3L);
        userDbStorage.deleteFriend(1L, 2L);
        assertEquals(List.of(user), userDbStorage.findCommonFriends(1L, 2L));
    }
}