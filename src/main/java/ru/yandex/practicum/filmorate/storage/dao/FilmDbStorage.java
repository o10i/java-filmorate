package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Repository("filmStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film saveFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS(NAME, RELEASE_DATE, DESCRIPTION, DURATION, RATE, MPA) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setDate(2, Date.valueOf(film.getReleaseDate()));
            ps.setString(3, film.getDescription());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setLong(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sql = "INSERT INTO FILM_GENRE values (?, ?)";
                jdbcTemplate.update(sql,
                        id,
                        genre.getId());
            }
        }
        return findFilmById(id);
    }

    @Override
    public Film findFilmById(Long id) {
        String sqlQuery = "SELECT * FROM FILMS F LEFT JOIN MPA M ON F.MPA = M.ID WHERE F.ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Фильм с id=%d не найден.", id)));
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "SELECT * FROM FILMS F LEFT JOIN MPA M ON F.MPA = M.ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION = ?, RATE = ?, MPA = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                sqlQuery = "merge into FILM_GENRE key(FILM_ID, GENRE_ID) values (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
        return findFilmById(film.getId());
    }

    @Override
    public boolean saveLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO LIKES(FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return true;
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sqlQuery, id, userId) < 1) {
            throw new ObjectNotFoundException(String.format("Пользователь с id=%d не ставил лайк фильму с id=%d.", userId, id));
        }
        return true;
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        String sqlQuery = "SELECT * FROM FILMS F LEFT JOIN MPA M ON F.MPA = M.ID LEFT JOIN LIKES L ON L.FILM_ID = F.ID GROUP BY F.ID ORDER BY COUNT(l.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(Mpa.builder()
                        .id(resultSet.getLong("mpa.id"))
                        .name(resultSet.getString("mpa.name"))
                        .build())
                .genres(findFilmGenresByFilmId(resultSet.getLong("id")))
                .likes(findUsersIdWhoLikedFilm(resultSet.getLong("id")))
                .build();
    }

    private List<Long> findUsersIdWhoLikedFilm(Long filmId) {
        String sqlQuery = "SELECT USER_ID from LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    private List<Genre> findFilmGenresByFilmId(Long filmId) {
        String sqlQuery = "select * from GENRES where ID in (select GENRE_ID from FILM_GENRE where FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRowToGenre, filmId);
    }
}
