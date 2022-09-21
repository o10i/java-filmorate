package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceprion.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    FilmController fc = new FilmController();

    @Test
    void create() {
        Film film = Film.builder()
                .name("Test")
                .description("create")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        fc.create(film);
        Film savedFilm = fc.getAll().get(0);
        assertEquals(1, fc.getAll().size(), "Количество фильмов не совпадает.");
        assertEquals(film, savedFilm, "Фильм не добавился.");
    }

    @Test
    void createWithFailedName() {
        Film film = Film.builder()
                .name("")
                .description("createWithFailedName")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        assertThrows(ValidationException.class, () -> fc.create(film), "Название фильма не может быть пустым.");
    }

    @Test
    void createWithFailedDescription() {
        Film film = Film.builder()
                .name("createWithFailedDescription")
                .description("123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890123456789012345678901")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        assertThrows(ValidationException.class, () -> fc.create(film), "Максимальная длина описания фильма — 200 символов.");
    }

    @Test
    void createWithFailedReleaseDate() {
        Film film = Film.builder()
                .name("Test")
                .description("createWithFailedReleaseDate")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(1)
                .build();
        assertThrows(ValidationException.class, () -> fc.create(film), "Дата релиза должна быть не раньше 28 декабря 1895 года.");
    }

    @Test
    void createWithFailedDuration() {
        Film film = Film.builder()
                .name("Test")
                .description("createWithFailedDuration")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(0)
                .build();
        assertThrows(ValidationException.class, () -> fc.create(film), "Продолжительность фильма должна быть положительной.");
    }


    @Test
    void update() {
        Film film = Film.builder()
                .name("Test")
                .description("create")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        fc.create(film);
        Film updatedFilm = Film.builder()
                .id(1)
                .name("Test")
                .description("update")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
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

    @Test
    void getFilms() {
        Film film1 = Film.builder()
                .name("Test")
                .description("getFilms")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Film film2 = Film.builder()
                .name("Test")
                .description("getFilms")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(2)
                .build();
        fc.create(film1);
        fc.create(film2);
        assertEquals(2, fc.getAll().size(), "Количество фильмов не совпадает.");
    }
}