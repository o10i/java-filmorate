package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceprion.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
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

    private static void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        if (Duration.ofMinutes(film.getDuration()).isNegative() || Duration.ofMinutes(film.getDuration()).isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id " + film.getId() + " уже существует.");
        }
        film.setId(id++);
        films.put(film.getId(), film);
        log.debug("Фильм с id {} добавлен", film.getId());
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с id " + film.getId() + " не существует.");
        }
        films.put(film.getId(), film);
        log.debug("Фильм с id {} обновлён", film.getId());
        return film;
    }

    @GetMapping()
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
