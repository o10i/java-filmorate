package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public User saveUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping()
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable Long id) {
        return userService.findUserFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean saveFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.saveFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }
}