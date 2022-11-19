package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa findMpaById(Long id) {
        String sqlQuery = "select * from mpa where id = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("MPA с id " + id + " не найден.");
        }
        return mpa;
    }

    public List<Mpa> findAllMpa() {
        String sqlQuery = "select * from mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
