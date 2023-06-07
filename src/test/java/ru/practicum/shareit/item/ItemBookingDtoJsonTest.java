package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemBookingDtoJsonTest {
    private final JacksonTester<ItemBookingDto> json;

    @SneakyThrows
    @Test
    void fromItemBookingDtoTest() {
        BookingItemDto lastBooking = new BookingItemDto(
                0L,
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
        0L,
                Status.APPROVED
        );
        BookingItemDto nextBooking = new BookingItemDto(
                1L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                0L,
                Status.APPROVED
        );

        ItemBookingDto itemBookingDto = new ItemBookingDto(
                0L,
                "table",
                "Ivan's table",
                true,
                lastBooking,
                nextBooking,
                Collections.emptyList());

        JsonContent<ItemBookingDto> result = json.write(itemBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("table");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Ivan's table");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2024-01-01T13:00:00");
    }

    @SneakyThrows
    @Test
    void toItemBookingDtoTest() {
        BookingItemDto lastBooking = new BookingItemDto(
                0L,
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                0L,
                Status.APPROVED
        );
        BookingItemDto nextBooking = new BookingItemDto(
                1L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                0L,
                Status.APPROVED
        );

        ItemBookingDto itemBookingDto = new ItemBookingDto(
                0L,
                "table",
                "Ivan's table",
                true,
                lastBooking,
                nextBooking,
                Collections.emptyList());

        String jsonString = "{\"id\":0,\"name\":\"table\",\"description\":\"Ivan's table\",\"available\": true," +
                "\"lastBooking\":{\"id\": 0,\"start\":\"2023-01-01T12:00:00\",\"end\":\"2023-01-01T13:00:00\"," +
                "\"bookerId\":0,\"comments\":[]}," +
                "\"nextBooking\":{\"id\": 1,\"start\":\"2024-01-01T12:00:00\",\"end\":\"2024-01-01T13:00:00\"," +
                "\"bookerId\":0,\"comments\":[]}}";

        ItemBookingDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), itemBookingDto.getId());
        assertEquals(fromJson.getName(), itemBookingDto.getName());
        assertEquals(fromJson.getDescription(), itemBookingDto.getDescription());
        assertEquals(fromJson.getAvailable(), itemBookingDto.getAvailable());
        assertEquals(fromJson.getLastBooking(), itemBookingDto.getLastBooking());
    }
}
