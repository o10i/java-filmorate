package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    FilmController fc = new FilmController();

    @Test
    void update() {
        Film film = Film.builder()
                .name("Test")
                .description("create")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Film updatedFilm = Film.builder()
                .id(1)
                .name("Test")
                .description("update")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        fc.create(film);
        fc.update(updatedFilm);
        Film savedFilm = fc.getAll().get(0);
        assertEquals(1, fc.getAll().size(), "Количество фильмов не совпадает.");
        assertEquals(updatedFilm, savedFilm, "Фильм не обновился.");
    }

    @Test
    void updateWithUnknownId() {
        Film film = Film.builder()
                .name("Test")
                .description("create")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        fc.create(film);
        Film updatedFilm = Film.builder()
                .id(0)
                .name("Test")
                .description("updateWithUnknownId")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        assertThrows(ValidationException.class, () -> fc.update(updatedFilm), "Фильма с id " + updatedFilm.getId() + " не существует.");
    }
}