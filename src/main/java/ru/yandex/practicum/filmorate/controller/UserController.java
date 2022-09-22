package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUserId;
import static ru.yandex.practicum.filmorate.validation.UserValidation.validateUserName;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        validateUserId(users.containsKey(user.getId()), user, " уже зарегистрирован.");
        validateUserName(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь с id {} зарегистрирован", user.getId());
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        validateUserId(!users.containsKey(user.getId()), user, " не зарегистрирован.");
        validateUserName(user);
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлён", user.getId());
        return user;
    }

    @GetMapping()
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
