package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public void addLike(Film film, User user) {
        filmStorage.getAll().stream().filter(f -> f.equals(film)).forEach(f -> f.getLikes().add(user.getId()));
    }

    public void removeLike(Film film, User user) {
        filmStorage.getAll().stream().filter(f -> f.equals(film)).forEach(f -> f.getLikes().remove(user.getId()));
    }

    public List<Film> getTenMostPopularFilms() {
        ArrayList<Film> tenMostPopularFilms = new ArrayList<>();
        List<Film> allFilms = filmStorage.getAll();
        allFilms.sort(Comparator.comparing(film -> film.getLikes().size()));
        for (int i = 0; i < allFilms.size(); i++) {
            if (i == 10) {
                break;
            }
            tenMostPopularFilms.add(allFilms.get(i));
        }
        return tenMostPopularFilms;
    }
}
