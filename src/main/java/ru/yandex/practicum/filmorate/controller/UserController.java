package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceprion.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    private static void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private static void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю с id {} присвоено новое имя {}", user.getId(), user.getLogin());
        }
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id " + user.getId() + " уже зарегистрирован.");
        }
        validateName(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.debug("Пользователь с id {} зарегистрирован", user.getId());
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        validateName(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователя с id " + user.getId() + " не существует.");
        }
        users.put(user.getId(), user);
        log.debug("Пользователь с id {} обновлён", user.getId());
        return user;
    }

    @GetMapping()
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
