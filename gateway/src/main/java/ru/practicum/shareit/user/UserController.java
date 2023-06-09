package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users");
        return userClient.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> add(@Validated(UserDto.New.class) @RequestBody UserDto userDto) {
        log.info("Trying to create new user");
        return userClient.add(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {
        log.info("Trying to fetch user with id = {}", userId);
        return userClient.get(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @Validated(UserDto.UpdateFields.class) @RequestBody UserDto userDto) {
        log.info("Trying to update user with id = {}", userId);
        userDto.setId(userId);
        return userClient.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        log.info("Trying to delete user with id = {}", userId);
        return userClient.delete(userId);
    }
}
