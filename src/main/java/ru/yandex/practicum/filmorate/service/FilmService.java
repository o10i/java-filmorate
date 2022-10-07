package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        validateFilmId(filmStorage.getFilms().containsKey(film.getId()), film, " уже существует.");
        Film createdFilm = filmStorage.create(film);
        log.info("Фильм с id {} добавлен", film.getId());
        return createdFilm;
    }

    public Film update(Film film) {
        validateFilmId(!filmStorage.getFilms().containsKey(film.getId()), film, " не существует.");
        Film updatedFilm = filmStorage.update(film);
        log.info("Фильм с id {} обновлён", film.getId());
        return updatedFilm;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilms().get(id);
    }

    public void addLike(Long id, Long userId) {
        getFilmById(id).getLikes().add(userId);
    }

    public void removeLike(Long id, Long userId) {
        getFilmById(id).getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        ArrayList<Film> mostPopularFilms = new ArrayList<>();
        List<Film> allFilms = filmStorage.getAll();
        allFilms.sort(Comparator.comparing(film -> film.getLikes().size()));
        for (int i = 0; i < allFilms.size(); i++) {
            if (i == count) {
                break;
            }
            mostPopularFilms.add(allFilms.get(i));
        }
        return mostPopularFilms;
    }

    private void validateFilmId(boolean films, Film film, String x) {
        if (films) {
            throw new ValidationException("Фильм с id " + film.getId() + x);
        }
    }
}
