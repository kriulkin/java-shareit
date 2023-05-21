package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private long currentId;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User add(User user) {
        if (users.containsValue(user)) {
            return null;
        }

        long userId = getCurrentId();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User update(User user) {
        long userId = user.getId();

        if (users.containsValue(user) && !users.get(userId).equals(user)) {
            return null;
        }

        User currentUser = users.get(userId);

        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }

        return currentUser;
    }

    @Override
    public User delete(long id) {
        return users.remove(id);
    }

    @Override
    public User get(long id) {
        return users.get(id);
    }

    private long getCurrentId() {
        return ++currentId;
    }
}
