package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidation {
    public static void validateUserId(boolean users, User user, String x) {
        if (users) {
            throw new ValidationException("Пользователь с id " + user.getId() + x);
        }
    }

    public static void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователю с id {} присвоено новое имя {}", user.getId(), user.getLogin());
        }
    }
}
