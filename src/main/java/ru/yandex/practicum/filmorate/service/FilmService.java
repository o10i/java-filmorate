package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film saveFilm(Film film) {
        return filmStorage.saveFilm(film);
    }

    public Film updateFilm(Film film) {
        findFilmById(film.getId());
        return filmStorage.updateFilm(film);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    public boolean saveLike(Long id, Long userId) {
        findFilmById(id);
        return filmStorage.saveLike(id, userId);
    }

    public boolean deleteLike(Long id, Long userId) {
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.findPopularFilms(count);
    }
}
