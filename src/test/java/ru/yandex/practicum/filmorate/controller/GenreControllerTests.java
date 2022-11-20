package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTests {
    private final GenreDbStorage genreDbStorage;
    @Test
    void testFindGenreById() {
        assertEquals(Genre.builder().id(1L).name("Комедия").build(), genreDbStorage.findGenreById(1L));
    }

    @Test
    void testFindUnknownGenre() {
        assertThrows(ObjectNotFoundException.class, () -> genreDbStorage.findGenreById(-1L), "Genre с id " + -1 + " не найден.");
    }

    @Test
    void testFindAllGenres() {
        assertEquals(Genre.builder().id(1L).name("Комедия").build(), genreDbStorage.findAllGenres().get(0));
        assertEquals(6, genreDbStorage.findAllGenres().size());
    }
}
