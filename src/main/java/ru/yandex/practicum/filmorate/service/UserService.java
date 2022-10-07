package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(long id, long friendId) {
        userStorage.getUserById(id).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(id);
    }

    public void removeFriend(long id, long friendId) {
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
    }

    public List<User> getUserFriends(long id) {
        List<User> friendsList = new ArrayList<>();
        Set<Long> friendsId = userStorage.getUserById(id).getFriends();
        for (Long friendId : friendsId) {
            friendsList.add(userStorage.getUserById(friendId));
        }
        return friendsList;
    }

    public List<User> getMutualFriends(long id, long otherId) {
        List<User> userFriends = getUserFriends(id);
        List<User> otherUserFriends = getUserFriends(otherId);
        return userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());
    }
}
