package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id " + film.getId() + " уже существует.");
        }
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм с id {} добавлен", film.getId());
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с id " + film.getId() + " не существует.");
        }
        films.put(film.getId(), film);
        log.info("Фильм с id {} обновлён", film.getId());
        return film;
    }

    @GetMapping()
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
