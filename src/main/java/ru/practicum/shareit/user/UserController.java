package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей {}", userService.findAll().size());
        return userService.findAll();
    }

    @PostMapping
    public UserDto add(@Validated(UserDto.New.class) @RequestBody UserDto userDto) {
        log.info("Попытка добавить пользователя");
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Попытка получить пользователя с id = {}", userId);
        return userService.get(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId,
                          @Validated(UserDto.UpdateFields.class) @RequestBody UserDto userDto) {
        log.info("Попытка обновить пользователя с id = {}", userId);
        userDto.setId(userId);
        return userService.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Попытка удалить пользователя с id = {}", userId);
        userService.delete(userId);
    }
}
