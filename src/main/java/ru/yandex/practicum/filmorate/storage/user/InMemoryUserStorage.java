package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User saveUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        List<User> userFriends = findUserFriends(id);
        List<User> otherUserFriends = findUserFriends(otherId);
        return userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());

    }

    @Override
    public List<User> findUserFriends(Long id) {
        List<Long> friendsId = findUserById(id).getFriends();
        List<User> friends = new ArrayList<>();
        if (friendsId != null) {
            friends = friendsId.stream().map(this::findUserById).collect(Collectors.toList());
        }
        return friends;
    }

    @Override
    public boolean saveFriend(Long id, Long friendId) {
        User userId = findUserById(id);
        if (userId.getFriends() == null) {
            userId.setFriends(new ArrayList<>());
        }
        User userFriendId = findUserById(friendId);
        if (userFriendId.getFriends() == null) {
            userFriendId.setFriends(new ArrayList<>());
        }
        userId.getFriends().add(friendId);
        userFriendId.getFriends().add(id);
        return true;
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        findUserById(id).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(id);
        return true;
    }
}
