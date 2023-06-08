package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerIntTest {

    @MockBean
    BookingServiceImpl bookingService;
    private final MockMvc mvc;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @SneakyThrows
    @Test
    void addTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:01"),
                LocalDateTime.parse("2024-01-01T13:00:01")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingService.add(anyLong(), any())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(newBookingDto))
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(newBookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(newBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(newBookingDto.getEnd().toString())));
    }

    @SneakyThrows
    @Test
    void getExistedItem() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:01"),
                LocalDateTime.parse("2024-01-01T13:00:01")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(newBookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(newBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(newBookingDto.getEnd().toString())));
    }

    @SneakyThrows
    @Test
    void getNonExistedItem() {
        when(bookingService.get(anyLong(), anyLong())).thenThrow(NoSuchEntityException.class);

        mvc.perform(get("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateStatusExistedItemTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:01"),
                LocalDateTime.parse("2024-01-01T13:00:01")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 0L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(newBookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(newBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(newBookingDto.getEnd().toString())));
    }

    @SneakyThrows
    @Test
    void updateStatusNonExistedItemTest() {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenThrow(NoSuchEntityException.class);

        mvc.perform(patch("/bookings/{bookingId}", 0L)
                        .header("X-Sharer-User-Id", 0L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getByBookerTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto1 = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:01"),
                LocalDateTime.parse("2024-01-01T13:00:01")
        );
        NewBookingDto newBookingDto2 = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-02-01T12:00:01"),
                LocalDateTime.parse("2024-02-01T13:00:01")
        );

        Booking booking1 = BookingMapper.toBooking(newBookingDto1, user, item);
        Booking booking2 = BookingMapper.toBooking(newBookingDto2, user, item);
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking1);
        BookingDto bookingDto2 = BookingMapper.toBookingDto(booking2);

        when(bookingService.getByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 0L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "25")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(newBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(newBookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(newBookingDto1.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(newBookingDto1.getEnd().toString())))
                .andExpect(jsonPath("$[1].id", is(newBookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(newBookingDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(newBookingDto2.getStart().toString())))
                .andExpect(jsonPath("$[1].end", is(newBookingDto2.getEnd().toString())));
    }

    @SneakyThrows
    @Test
    void getByOwnerValidTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto1 = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:01"),
                LocalDateTime.parse("2024-01-01T13:00:01")
        );
        NewBookingDto newBookingDto2 = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-02-01T12:00:01"),
                LocalDateTime.parse("2024-02-01T13:00:01")
        );

        Booking booking1 = BookingMapper.toBooking(newBookingDto1, user, item);
        Booking booking2 = BookingMapper.toBooking(newBookingDto2, user, item);
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking1);
        BookingDto bookingDto2 = BookingMapper.toBookingDto(booking2);

        when(bookingService.getByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 0L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "25")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(newBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(newBookingDto1.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(newBookingDto1.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(newBookingDto1.getEnd().toString())))
                .andExpect(jsonPath("$[1].id", is(newBookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(newBookingDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(newBookingDto2.getStart().toString())))
                .andExpect(jsonPath("$[1].end", is(newBookingDto2.getEnd().toString())));
    }
}
