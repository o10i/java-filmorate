package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
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
        return genre;
    }

    public List<Genre> findFilmGenresByFilmId(Long id) {
        String sqlQuery = "select * from GENRES where ID in (select GENRE_ID from FILM_GENRE where FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
    }

    public List<Genre> findAllGenres() {
        String sqlQuery = "select * from GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
