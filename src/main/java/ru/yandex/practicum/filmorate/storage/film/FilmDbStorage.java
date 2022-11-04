package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    @Override
    public Film saveFilm(Film film) {
        String sqlQuery = "insert into FILMS(NAME, RELEASE_DATE, DESCRIPTION, DURATION, RATE, MPA) values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setDate(2, Date.valueOf(film.getReleaseDate()));
            ps.setString(3, film.getDescription());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setInt(6, Math.toIntExact(film.getMpa().getId()));
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sql = "insert into FILM_GENRE values (?, ?)";
                jdbcTemplate.update(sql,
                        id,
                        genre.getId());
                log.info("Жанры фильма с id = {} обновлены.", film.getId());
            }
        }
        return findFilmById(id);
    }

    @Override
    public Film findFilmById(Long id) {
        String sqlQuery = "select * from films where id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(String.format("Фильм с id=%d не найден.", id));
        }
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION = ?, RATE = ?, MPA = ? where ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            sqlQuery = "delete from FILM_GENRE where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                sqlQuery = "merge into FILM_GENRE key(FILM_ID, GENRE_ID) values (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
                log.info("Жанры фильма с id = {} обновлены.", film.getId());
            }
        }
        return findFilmById(film.getId());
    }

    @Override
    public boolean saveLike(Long id, Long userId) {
        String sqlQuery = "insert into LIKES(FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        return true;
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        String sqlQuery = "select f.* from FILMS f left join LIKES l on l.FILM_ID = f.ID group by f.ID order by count(l.USER_ID) desc limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        return jdbcTemplate.update(sqlQuery, id, userId) > 0;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(mpaService.findMpaById(Long.valueOf(resultSet.getString("mpa"))))
                .genres(genreService.findFilmGenresByFilmId(resultSet.getLong("id")))
                .likes(findUsersIdWhoLikedFilm(resultSet.getLong("id")))
                .build();
    }

    private List<Long> findUsersIdWhoLikedFilm(Long filmId) {
        String sqlQuery = "select USER_ID from LIKES where FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }
}
