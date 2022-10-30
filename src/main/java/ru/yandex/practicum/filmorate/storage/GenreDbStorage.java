package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre findGenreById(Long id) {
        String sqlQuery = "select * from GENRES where id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(String.format("Жанр с id=%d не найден.", id));
        }
        log.info("Найден жанр c id = {}", id);
        return genre;
    }

    public List<Genre> findFilmGenresByFilmId(Long id) {
        String sqlQuery = "select * from GENRES where ID in (select GENRE_ID from FILM_GENRE where FILM_ID = ?)";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        log.info("Найдены жанры фильма c id={}", id);
        return genres;
    }

    public List<Genre> findAllGenres() {
        String sqlQuery = "select * from GENRES";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        log.info("Все жанры найдены.");
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
