package ru.yandex.practicum.filmorate.exceprion;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
