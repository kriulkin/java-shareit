package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerIntTest {
    @MockBean
    private final UserService service;
    private final MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Test
    void findAllWithEmptyUserListTest() {
        when(service.findAll()).thenReturn(Collections.emptyList());

        String output = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(output, mapper.writeValueAsString(Collections.emptyList()));
    }

    @SneakyThrows
    @Test
    void findAllTest() {
        List<User> users = List.of(
                new User(0L, "Ivan", "ivan@mail.com"),
                new User(1L, "Petr", "petr@mail.com"),
                new User(2L, "Boris", "boris@mail.com")
        );

        when(service.findAll()).thenReturn(users);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(users.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(users.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(users.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(users.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(users.get(1).getName())))
                .andExpect(jsonPath("$[1].email", is(users.get(1).getEmail())))
                .andExpect(jsonPath("$[2].id", is(users.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(users.get(2).getName())))
                .andExpect(jsonPath("$[2].email", is(users.get(2).getEmail())));
    }

    @SneakyThrows
    @Test
    void getExistedUser() {
        UserDto userDto = new UserDto(0L, "Ivan", "ivan@mail.com");

        when(service.get(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 0)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void getNonExistedUser() {
        when(service.get(anyLong())).thenThrow(NoSuchEntityException.class);

        mvc.perform(get("/users/{userId}", 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addUser() {
        UserDto userDto = new UserDto(0L, "Ivan", "ivan@mail.com");

        when(service.add(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void addUserWithIncorrectEmail() {
        UserDto userDto = new UserDto(0L, "Ivan", "ivanmail.com");

        when(service.add(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addUserWithoutEmail() {
        UserDto userDto = new UserDto(0L, "Ivan", null);

        when(service.add(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateTest() {
        UserDto userDto = new UserDto(0L, "Ivan", "ivan@mail.com");

        when(service.update(any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", 0L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void deleteTest() {
        doNothing().when(service).delete(anyLong());

        mvc.perform(delete("/users/{userId}", 0L))
                .andExpect(status().isOk());
    }
}
