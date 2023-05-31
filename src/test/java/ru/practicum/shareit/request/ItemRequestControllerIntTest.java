package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerIntTest {

    @MockBean
    ItemRequestServiceImpl requestService;
    private final MockMvc mvc;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @SneakyThrows
    @Test
    void addTest() {
        User user = new User(null, "Ivan", "ivan@mail.com");
        NewItemRequestDto newRequestDto = new NewItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:01")
        );

        ItemRequest request = ItemRequestMapper.toItemRequest(user, newRequestDto);
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        when(requestService.add(anyLong(), any())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequestDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(newRequestDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void addRequestWithEmptyDescription() {
        User user = new User(null, "Ivan", "ivan@mail.com");
        NewItemRequestDto newRequestDto = new NewItemRequestDto(
                0L,
                "",
                LocalDateTime.parse("2023-01-01T12:00:01")
        );

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequestDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user);
        NewItemRequestDto newRequestDto = new NewItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:01")
        );

        ItemRequest request = ItemRequestMapper.toItemRequest(user, newRequestDto);
        AnsweredItemRequestDto requestDto = ItemRequestMapper.toAnsweredItemRequestDto(request, List.of(item));

        when(requestService.get(anyLong(), anyLong())).thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", 0L)
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(newRequestDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void findAllButNotUserIdTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        AnsweredItemRequestDto request1 = new AnsweredItemRequestDto(
                null,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00"),
                Collections.emptyList()
        );
        AnsweredItemRequestDto request2 = new AnsweredItemRequestDto(
                null,
                "pencil",
                LocalDateTime.parse("2024-01-01T12:00:00"),
                Collections.emptyList()
        );
        AnsweredItemRequestDto request3 = new AnsweredItemRequestDto(
                null,
                "car",
                LocalDateTime.parse("2024-01-01T12:00:00"),
                Collections.emptyList()
        );

        when(requestService.findAllButNotUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(request1, request2, request3));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(request2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(request2.getDescription())))
                .andExpect(jsonPath("$[2].id", is(request3.getId()), Long.class))
                .andExpect(jsonPath("$[2].description", is(request3.getDescription())));
    }

    @SneakyThrows
    @Test
    void findByUserIdTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        AnsweredItemRequestDto request1 = new AnsweredItemRequestDto(
                null,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00"),
                Collections.emptyList()
        );
        AnsweredItemRequestDto request2 = new AnsweredItemRequestDto(
                null,
                "pencil",
                LocalDateTime.parse("2024-01-01T12:00:00"),
                Collections.emptyList()
        );
        AnsweredItemRequestDto request3 = new AnsweredItemRequestDto(
                null,
                "car",
                LocalDateTime.parse("2024-01-01T12:00:00"),
                Collections.emptyList()
        );

        when(requestService.findByUserId(anyLong())).thenReturn(List.of(request1, request2, request3));

        mvc.perform(get("/requests/")
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(request2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(request2.getDescription())))
                .andExpect(jsonPath("$[2].id", is(request3.getId()), Long.class))
                .andExpect(jsonPath("$[2].description", is(request3.getDescription())));
    }
}
