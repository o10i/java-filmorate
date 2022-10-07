package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    protected final Map<Long, Film> films = new HashMap<>();
    protected long id = 1;

    @Override
    public Film create(Film film) {
        validateFilmId(films.containsKey(film.getId()), film, " уже существует.");
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм с id {} добавлен", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilmId(!films.containsKey(film.getId()), film, " не существует.");
        films.put(film.getId(), film);
        log.info("Фильм с id {} обновлён", film.getId());
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    private void validateFilmId(boolean films, Film film, String x) {
        if (films) {
            throw new ValidationException("Фильм с id " + film.getId() + x);
        }
    }
}