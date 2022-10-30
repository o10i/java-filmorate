package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film saveFilm(Film film) {
        Film savedFilm = filmStorage.saveFilm(film);
        log.debug("Фильм '{}' с id={} добавлен.", film.getName(), film.getId());
        return savedFilm;
    }

    public Film findFilmById(Long id) {
        Film film = filmStorage.findFilmById(id);
        log.debug("Фильм '{}' с id={} найден.", film.getName(), film.getId());
        return film;
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        log.debug("Все фильмы найдены.");
        return films;
    }

    public Film updateFilm(Film film) {
        filmStorage.findFilmById(film.getId());
        Film updatedFilm = filmStorage.updateFilm(film);
        log.debug("Фильм с id={} обновлён.", film.getId());
        return updatedFilm;
    }


    public void saveLike(Long id, Long userId) {
        filmStorage.saveLike(id, userId);
        log.debug("Пользователь с id={} поставил лайк на фильм с id={}.", userId, id);
    }

    public List<Film> findPopularFilms(Integer count) {
        List<Film> popularFilms = filmStorage.findPopularFilms(count);
        log.debug("{} наиболее популярных фильмов возвращены.", count);
        return popularFilms;
    }

    public boolean deleteLike(Long id, Long userId) {
        if (!filmStorage.deleteLike(id, userId)) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%d не ставил лайк фильму с id=%d.", userId, id));
        }
        log.debug("Пользователь с id={} удалил лайк с фильма с id={}.", userId, id);
        return true;
    }
}
