package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    // этот метод необходимо перенести из контроллера? как лучше назвать пакет?
    private static void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователю с id {} присвоено новое имя {}", user.getId(), user.getLogin());
        }
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id " + user.getId() + " уже зарегистрирован.");
        }
        checkName(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь с id {} зарегистрирован", user.getId());
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        checkName(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователя с id " + user.getId() + " не существует.");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлён", user.getId());
        return user;
    }

    @GetMapping()
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
