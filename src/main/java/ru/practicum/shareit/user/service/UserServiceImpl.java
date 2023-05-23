package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findById(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId)));
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = userStorage.save(UserMapper.toUser(userDto));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto get(long userId) {
        return UserMapper.toUserDto(findById(userId));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userStorage.findById(userDto.getId()).
                orElseThrow(() -> new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userDto.getId())));

        User updatedUser = UserMapper.toUser(userDto);

        if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            updatedUser.setEmail(user.getEmail());
        }

        if (updatedUser.getName() == null || updatedUser.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }

        return UserMapper.toUserDto(userStorage.save(updatedUser));
    }

    @Override
    public void delete(long userId) {
        User user = userStorage.findById(userId).
                orElseThrow(() -> new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId)));

        userStorage.delete(user);
    }
}
