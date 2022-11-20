package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    private User getUser() {
        return User.builder().email("test@mail.ru").login("testLogin").name("testName").birthday(Date.valueOf("1946-08-20").toLocalDate()).friends(new ArrayList<>()).build();
    }

    private Film getFilm() {
        return Film.builder().name("testName").releaseDate(Date.valueOf("1979-04-17").toLocalDate()).description("testDescription").duration(100).rate(4).mpa(Mpa.builder().id(1L).name("G").build()).genres(new ArrayList<>()).likes(new ArrayList<>()).build();
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FILM_GENRE");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH 1");

    }

    @Test
    void testSaveFilm() {
        Film film = getFilm();
        Film savedFilm = filmDbStorage.saveFilm(film);
        film.setId(1L);
        assertEquals(film, savedFilm);
    }

    @Test
    void testSaveFilmWithEmptyName() {
        Film film = getFilm();
        film.setName("");
        assertThrows(DataIntegrityViolationException.class, () -> filmDbStorage.saveFilm(film), "Имя не должно быть пустым.");
    }

    @Test
    void testSaveFilmWithLongDescription() {
        Film film = getFilm();
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " + "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " + "который за время «своего отсутствия», стал кандидатом Коломбани.");
        assertThrows(DataIntegrityViolationException.class, () -> filmDbStorage.saveFilm(film), "Описание не должно иметь более 200 символов.");
    }

    @Test
    void testSaveFilmWithOldReleaseDate() {
        Film film = getFilm();
        film.setReleaseDate(Date.valueOf("1895-12-27").toLocalDate());
        assertThrows(DataIntegrityViolationException.class, () -> filmDbStorage.saveFilm(film), "Дата выхода не может быть раньше 28.12.1895 г.");
    }

    @Test
    void testSaveFilmWithNotPositiveDuration() {
        Film film = getFilm();
        film.setDuration(0);
        assertThrows(DataIntegrityViolationException.class, () -> filmDbStorage.saveFilm(film), "Продолжительность должна быть положительной.");
    }

    @Test
    void testSaveFilmWithNullMpa() {
        Film film = getFilm();
        film.setMpa(null);
        assertThrows(NullPointerException.class, () -> filmDbStorage.saveFilm(film), "MPA не может быть 'null'.");
    }

    @Test
    void testUpdateFilm() {
        Film film = filmDbStorage.saveFilm(getFilm());
        film.setName("testUpdateName");
        assertEquals(film, filmDbStorage.updateFilm(film));
    }

    @Test
    void testUpdateUnknownFilm() {
        Film film = getFilm();
        film.setId(9999L);
        assertThrows(ObjectNotFoundException.class, () -> filmDbStorage.updateFilm(film), "Фильм с id " + film.getId() + " не найден.");
    }

    @Test
    void testFindAllFilms() {
        Film film = filmDbStorage.saveFilm(getFilm());
        List<Film> films = List.of(film);
        assertEquals(films, filmDbStorage.findAllFilms());
    }

    @Test
    void testFindFilmById() {
        Film film = filmDbStorage.saveFilm(getFilm());
        Film film2 = filmDbStorage.saveFilm(getFilm());
        Film film3 = filmDbStorage.saveFilm(getFilm());
        assertEquals(film, filmDbStorage.findFilmById(1L));
        assertEquals(film2, filmDbStorage.findFilmById(2L));
        assertEquals(film3, filmDbStorage.findFilmById(3L));
    }

    @Test
    void testFindUnknownFilm() {
        assertThrows(ObjectNotFoundException.class, () -> filmDbStorage.findFilmById(9999L), "Фильм с id " + 9999 + " не найден.");
    }

    @Test
    void testSaveLike() {
        userDbStorage.saveUser(getUser());
        filmDbStorage.saveFilm(getFilm());
        filmDbStorage.saveFilm(getFilm());
        assertTrue(filmDbStorage.saveLike(2L, 1L));
    }

    @Test
    void testFindEmptyPopularFilms() {
        assertEquals(new ArrayList<>(), filmDbStorage.findPopularFilms(10));
    }

    @Test
    void testFindOnePopularFilm() {
        userDbStorage.saveUser(getUser());
        filmDbStorage.saveFilm(getFilm());
        Film film = filmDbStorage.saveFilm(getFilm());
        filmDbStorage.saveLike(2L, 1L);
        film.setLikes(List.of(1L));
        assertEquals(List.of(film), filmDbStorage.findPopularFilms(1));
    }

    @Test
    void testFindTwoPopularFilms() {
        Film film = filmDbStorage.saveFilm(getFilm());
        Film film2 = filmDbStorage.saveFilm(getFilm());
        assertEquals(List.of(film, film2), filmDbStorage.findPopularFilms(10));
    }

    @Test
    void testDeleteLike() {
        userDbStorage.saveUser(getUser());
        filmDbStorage.saveFilm(getFilm());
        filmDbStorage.saveLike(1L, 1L);
        assertTrue(filmDbStorage.deleteLike(1L, 1L));
    }

    @Test
    void testDeleteUnknownUserLike() {
        filmDbStorage.saveFilm(getFilm());
        assertThrows(ObjectNotFoundException.class,() -> filmDbStorage.deleteLike(1L, -2L), "Пользователь с id=-2 не ставил лайк фильму с id=1");
    }

    @Test
    void testUpdateFilmWithGenre() {
        Film film = filmDbStorage.saveFilm(getFilm());
        film.setGenres(List.of(Genre.builder().id(2L).name("Драма").build()));
        assertEquals(film, filmDbStorage.updateFilm(film));
    }

    @Test
    void testFindFilmWithGenre() {
        Film film = getFilm();
        film.setGenres(List.of(Genre.builder().id(2L).name("Драма").build()));
        Film savedFilm = filmDbStorage.saveFilm(film);
        assertEquals(savedFilm, filmDbStorage.findFilmById(1L));
    }

    @Test
    void testFindAllFilmsWithGenre() {
        Film film = getFilm();
        Film film2 = getFilm();
        film.setGenres(List.of(Genre.builder().id(2L).name("Драма").build()));
        film2.setGenres(List.of(Genre.builder().id(2L).name("Драма").build()));
        Film savedFilm = filmDbStorage.saveFilm(film);
        Film savedFilm2 = filmDbStorage.saveFilm(film2);
        assertEquals(List.of(savedFilm, savedFilm2), filmDbStorage.findAllFilms());
    }

    @Test
    void testFindFilmWithoutGenres() {
        Film savedFilm = filmDbStorage.saveFilm(getFilm());
        assertEquals(savedFilm.getGenres(), filmDbStorage.findAllFilms().get(0).getGenres());
    }

    @Test
    void testFindFilmWithThreeGenres() {
        Film film = getFilm();
        film.setGenres(List.of(Genre.builder().id(1L).name("Комедия").build(), Genre.builder().id(2L).name("Драма").build(), Genre.builder().id(3L).name("Мультфильм").build()));
        Film savedFilm = filmDbStorage.saveFilm(film);
        assertEquals(savedFilm.getGenres().size(), filmDbStorage.findAllFilms().get(0).getGenres().size());
    }

    @Test
    void testUpdateFilmWithRepeatedGenres() {
        Film film = filmDbStorage.saveFilm(getFilm());
        film.setGenres(List.of(Genre.builder().id(1L).name("Комедия").build(), Genre.builder().id(2L).name("Драма").build(), Genre.builder().id(1L).name("Комедия").build()));
        Film updatedFilm = filmDbStorage.updateFilm(film);
        film.setGenres(List.of(Genre.builder().id(1L).name("Комедия").build(), Genre.builder().id(2L).name("Драма").build()));
        assertEquals(film, updatedFilm);
    }
}