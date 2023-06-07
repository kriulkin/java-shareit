package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void fromBookingDtoTest() {
        BookingDto bookingDto = new BookingDto(
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                null,
                null,
                Status.APPROVED
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-01T13:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @SneakyThrows
    @Test
    void toBookingDto() {
        BookingDto bookingDto = new BookingDto(
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                null,
                null,
                Status.APPROVED
        );
        String jsonString = "{\"id\":0,\"start\":\"2024-01-01T12:00:00\",\"end\":\"2024-01-01T13:00:00\",\"status\":\"APPROVED\"}";

        BookingDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), bookingDto.getId());
        assertEquals(fromJson.getStart(), bookingDto.getStart());
        assertEquals(fromJson.getEnd(), bookingDto.getEnd());
        assertEquals(fromJson.getStatus(), bookingDto.getStatus());
    }
}
