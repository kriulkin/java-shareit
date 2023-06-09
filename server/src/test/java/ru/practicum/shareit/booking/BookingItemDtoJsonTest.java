package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingItemDtoJsonTest {
    private final JacksonTester<BookingItemDto> json;

    @SneakyThrows
    @Test
    void fromBookingItemDtoTest() {
        BookingItemDto bookingItemDto = new BookingItemDto(
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                0L,
                Status.APPROVED
        );

        JsonContent<BookingItemDto> result = json.write(bookingItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-01T13:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @SneakyThrows
    @Test
    void toBookingItemDto() {
        BookingItemDto bookingItemDto = new BookingItemDto(
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                0L,
                Status.APPROVED
        );
        String jsonString = "{\"id\":0,\"start\":\"2024-01-01T12:00:00\",\"end\":\"2024-01-01T13:00:00\"," +
                "\"bookerId\":0,\"status\":\"APPROVED\"}";

        BookingItemDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), bookingItemDto.getId());
        assertEquals(fromJson.getStart(), bookingItemDto.getStart());
        assertEquals(fromJson.getEnd(), bookingItemDto.getEnd());
        assertEquals(fromJson.getBookerId(), bookingItemDto.getBookerId());
        assertEquals(fromJson.getStatus(), bookingItemDto.getStatus());
    }
}
