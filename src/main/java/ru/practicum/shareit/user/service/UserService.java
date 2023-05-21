package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> findAll();

    UserDto add(UserDto userDto);

    UserDto get(long userId);

    UserDto update(UserDto userDto);

    void delete(long userId);
}
