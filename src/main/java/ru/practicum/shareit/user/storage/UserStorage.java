package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User get(long id);

    User add(User user);

    User update(User user);

    User delete(long id);
}
