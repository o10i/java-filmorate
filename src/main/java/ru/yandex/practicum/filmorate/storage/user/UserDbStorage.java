package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User saveUser(User user) {
        String sqlQuery = "insert into USERS(EMAIL, LOGIN, NAME, BIRTHDAY) values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        log.info("Пользователь {} добавлен.", user.getName());
        return user;
    }

    @Override
    public User findUserById(Long id) {
        String sqlQuery = "select * from users where id = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        if (user == null) {
            log.info("Пользователь с id = {} не найден.", id);
        }
        log.info("Найден пользователь c id = {}", id);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "select * from users";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        log.info("Все пользователи найдены.");
        return users;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getName(),
                user.getId());
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        String sqlQuery = "delete from USERS where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("first_name"))
                .login(resultSet.getString("last_name"))
                .name(resultSet.getString("yearly_income"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
