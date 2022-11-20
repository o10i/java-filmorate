package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film saveFilm(Film film);

    Film findFilmById(Long id);

    List<Film> findAllFilms();

    Film updateFilm(Film film);

    List<Film> findPopularFilms(Integer count);

    boolean saveLike(Long id, Long userId);

    boolean deleteLike(Long id, Long userId);
}
