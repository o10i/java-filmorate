package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film saveFilm(Film film) {
        if (filmStorage.findFilmById(film.getId()) != null) {
            throw new FilmAlreadyExistException("Фильм с id " + film.getId() + " уже существует.");
        }
        Film savedFilm = filmStorage.saveFilm(film);
        log.debug("Фильм с id {} добавлен.", film.getId());
        return savedFilm;
    }

    public Film findFilmById(Long id) {
        Film film = filmStorage.findFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
        log.debug("Фильм с id {} найден.", id);
        return film;
    }

    public List<Film> findAllFilms() {
        log.debug("Все фильмы найдены.");
        return filmStorage.findAllFilms();
    }

    public Film updateFilm(Film film) {
        if (filmStorage.findFilmById(film.getId()) == null) {
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не существует.");
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        log.debug("Фильм с id {} обновлён.", film.getId());
        return updatedFilm;
    }

    public boolean deleteFilm(Long id) {
        if (filmStorage.findFilmById(id) == null) {
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
        log.debug("Фильм с id {} удалён.", id);
        return filmStorage.deleteFilm(id);
    }

    public void addLike(Long id, Long userId) {
        Film film = findFilmById(id);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
        log.debug("Пользователь с id {} поставил лайк на фильм с id {}.", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        Set<Long> likes = findFilmById(id).getLikes();
        if (!likes.contains(userId)) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не ставил лайк на этом фильме.");
        }
        likes.remove(userId);
        log.debug("Пользователь с id {} удалил лайк с фильма с id {}.", userId, id);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> allFilms = filmStorage.findAllFilms();
        for (Film film : allFilms) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
        }
        allFilms.sort(Comparator.comparing(film -> film.getLikes().size() * -1));
        if (allFilms.size() < count) {
            count = allFilms.size();
        }
        ArrayList<Film> mostPopularFilms = IntStream.range(0, count).mapToObj(allFilms::get).collect(Collectors.toCollection(ArrayList::new));
        log.debug("{} наиболее популярных фильмов возвращены.", count);
        return mostPopularFilms;
    }
}
