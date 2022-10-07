package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        validateUserId(users.containsKey(user.getId()), user, " уже зарегистрирован.");
        setUserNameAsLoginIfEmpty(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь с id {} зарегистрирован", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        validateUserId(!users.containsKey(user.getId()), user, " не зарегистрирован.");
        setUserNameAsLoginIfEmpty(user);
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлён", user.getId());
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    private void setUserNameAsLoginIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователю с id {} присвоено новое имя {}", user.getId(), user.getLogin());
        }
    }

    private void validateUserId(boolean users, User user, String x) {
        if (users) {
            throw new ValidationException("Пользователь с id " + user.getId() + x);
        }
    }
}
