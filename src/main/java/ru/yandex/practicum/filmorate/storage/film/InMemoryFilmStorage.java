package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    @Override
    public Film saveFilm(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void saveLike(Long id, Long userId) {
        Film film = findFilmById(id);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(userId);
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        List<Film> allFilms = findAllFilms();
        for (Film film : allFilms) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
        }
        allFilms.sort(Comparator.comparing(film -> film.getLikes().size() * -1));
        if (allFilms.size() < count) {
            count = allFilms.size();
        }
        return IntStream.range(0, count).mapToObj(allFilms::get).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        return findFilmById(id).getLikes().remove(userId);
    }
}