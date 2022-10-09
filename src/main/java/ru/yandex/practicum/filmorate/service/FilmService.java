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

    public Film create(Film film) {
        if (filmStorage.getFilms().containsKey(film.getId())) {
            throw new FilmAlreadyExistException("Фильм с id " + film.getId() + " уже существует.");
        }
        Film createdFilm = filmStorage.create(film);
        log.debug("Фильм с id {} добавлен.", film.getId());
        return createdFilm;
    }

    public Film update(Film film) {
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            log.debug("Фильм с id {} не существует.", film.getId());
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не существует.");
        }
        Film updatedFilm = filmStorage.update(film);
        log.debug("Фильм с id {} обновлён.", film.getId());
        return updatedFilm;
    }

    public List<Film> getAll() {
        log.debug("Все фильмы возвращены.");
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            log.debug("Фильм с id {} не существует.", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не существует.");
        }
        log.debug("Фильм с id {} возвращён.", id);
        return filmStorage.getFilms().get(id);
    }

    public void addLike(Long id, Long userId) {
        Film film = getFilmById(id);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
        log.debug("Пользователь с id {} поставил лайк на фильм с id {}.", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        Set<Long> likes = getFilmById(id).getLikes();
        if (!likes.contains(userId)) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не ставил лайк на этом фильме.");
        }
        likes.remove(userId);
        log.debug("Пользователь с id {} удалил лайк с фильма с id {}.", userId, id);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAll();
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
