package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerIntTest {
    @MockBean
    private final ItemServiceImpl itemService;
    private final MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Test
    void addItem() {
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, 0L);

        when(itemService.add(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @SneakyThrows
    @Test
    void getExistedItem() {
        ItemBookingDto itemDto = new ItemBookingDto(0L,
                "table",
                "Ivan's table",
                true,
                null,
                null,
                Collections.emptyList());

        when(itemService.get(anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 0)
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getNonExistedItem() {
        when(itemService.get(anyLong(), anyLong())).thenThrow(NoSuchEntityException.class);

        mvc.perform(get("/items/{userId}", 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateExistedItemTest() {
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, 0L);

        when(itemService.update(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 0)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @SneakyThrows
    @Test
    void updateNonExistedItem() {
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, 0L);

        when(itemService.update(anyLong(), any())).thenThrow(NoSuchEntityException.class);

        mvc.perform(patch("/items/{userId}", 0)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findByUserIdTest() {
        List<ItemBookingDto> items = List.of(
                new ItemBookingDto(0L,
                        "table",
                        "Ivan's table",
                        true,
                        null,
                        null,
                        Collections.emptyList()
                ),
                new ItemBookingDto(1L,
                        "pencil",
                        "Ivan's pencil",
                        true,
                        null,
                        null,
                        Collections.emptyList()
                )
        );

        when(itemService.findByUserId(anyLong(), anyInt(), anyInt())).thenReturn(items);

        mvc.perform(get("/items", 0)
                        .header("X-Sharer-User-Id", 0L)
                        .param("text", "ivan")
                        .param("from", "0")
                        .param("size", "25")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(items.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(items.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(items.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(items.get(0).getAvailable())))
                .andExpect(jsonPath("$[1].id", is(items.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(items.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(items.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(items.get(1).getAvailable())));
    }

    @SneakyThrows
    @Test
    void searchTest() {
        List<ItemDto> items = List.of(
                new ItemDto(0L,
                        "table",
                        "Ivan's table",
                        true,
                        0L
                ),
                new ItemDto(1L,
                        "pencil",
                        "Ivan's pencil",
                        true,
                        1L
                )
        );

        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(items);

        mvc.perform(get("/items/search", 0)
                        .header("X-Sharer-User-Id", 0L)
                        .param("text", "ivan")
                        .param("from", "0")
                        .param("size", "25")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(items.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(items.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(items.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(items.get(0).getAvailable())))
                .andExpect(jsonPath("$[1].id", is(items.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(items.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(items.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(items.get(1).getAvailable())));
    }

    @SneakyThrows
    @Test
    void addCommentToExistedItemTest() {
        CommentDto commentDto = new CommentDto(
                0L,
                "Nice table",
                "Ivan",
                null
        );

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 0L)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @SneakyThrows
    @Test
    void addCommentToNonExistedItem() {
        CommentDto commentDto = new CommentDto(
                0L,
                "Nice table",
                "Ivan",
                null
        );

        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(NoSuchEntityException.class);

        mvc.perform(post("/items/{itemId}/comment", 0L)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addIncorrectCommentTest() {
        CommentDto commentDto = new CommentDto(
                0L,
                "Nice table",
                "Ivan",
                null
        );

        when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(NotAvailableException.class);

        mvc.perform(post("/items/{itemId}/comment", 0L)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
