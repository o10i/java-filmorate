package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserValidation {
    public static void validateUserId(boolean users, User user, String x) {
        if (users) {
            throw new ValidationException("Пользователь с id " + user.getId() + x);
        }
    }
}
