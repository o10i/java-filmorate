package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User saveUser(User user) {
        String sqlQuery = "insert into USERS(EMAIL, LOGIN, NAME, BIRTHDAY) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findUserById(id);
    }

    @Override
    public User findUserById(Long id) {
        String sqlQuery = "select * from users where id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователь с id " + id + " не найден.");
        }
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return findUserById(user.getId());
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT * FROM USERS WHERE ID IN (SELECT f1.FRIEND_ID FROM FRIENDSHIP f1 JOIN FRIENDSHIP f2 ON f2.USER_ID = ? AND f2.FRIEND_ID = f1.FRIEND_ID WHERE f1.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
    }

    @Override
    public boolean saveFriend(Long id, Long friendId) {
        findUserById(id);
        findUserById(friendId);
        String sqlQuery = "insert into FRIENDSHIP(USER_ID, FRIEND_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
        return true;
    }

    @Override
    public List<User> findUserFriends(Long id) {
        String sqlQuery = "select * from USERS where ID in (select FRIEND_ID from FRIENDSHIP where USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        String sqlQuery = "delete from FRIENDSHIP where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
        return true;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(findUserFriendsId(resultSet.getLong("id")))
                .build();
    }

    private List<Long> findUserFriendsId(Long userId) {
        String sqlQuery = "select FRIEND_ID from FRIENDSHIP where USER_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
    }
}
