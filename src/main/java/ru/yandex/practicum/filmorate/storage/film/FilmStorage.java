package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film saveFilm(Film film);

    Film findFilmById(Long id);

    List<Film> findAllFilms();

    Film updateFilm(Film film);

    boolean deleteFilm(Long id);
}
