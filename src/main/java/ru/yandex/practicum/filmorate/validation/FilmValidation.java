package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmValidation {
    public static void validateFilmId(boolean films, Film film, String x) {
        if (films) {
            throw new ValidationException("Фильм с id " + film.getId() + x);
        }
    }
}
