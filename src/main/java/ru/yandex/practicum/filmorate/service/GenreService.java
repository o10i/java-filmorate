package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Genre findGenreById(Long id) {
        return genreDbStorage.findGenreById(id);
    }

    public List<Genre> findAllGenres() {
        return genreDbStorage.findAllGenres();
    }
}
