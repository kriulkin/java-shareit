package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NoSuchEntityException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public UserDto add(UserDto userDto) {
        User user = userStorage.add(UserMapper.toUser(userDto));

        if (user == null) {
            throw new AlreadyExistsException(
                    String.format("Пользватель с email %s уже существует", userDto.getEmail())
            );
        }

        return UserMapper.toUserDto(user);
    }

    public UserDto get(long userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            throw new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId));
        }

        return UserMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto) {
        User user = userStorage.get(userDto.getId());

        if (user == null) {
            throw new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userDto.getId()));
        }

        User updatedUser = userStorage.update(UserMapper.toUser(userDto));

        if (updatedUser == null) {
            throw new AlreadyExistsException(
                    String.format("Пользватель с email %s уже существует", userDto.getEmail())
            );
        }

        return UserMapper.toUserDto(updatedUser);
    }

    public void delete(long userId) {
        if (userStorage.delete(userId) == null) {
            throw new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId));
        }
    }
}
