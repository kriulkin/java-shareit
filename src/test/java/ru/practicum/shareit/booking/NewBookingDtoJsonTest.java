package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NewBookingDtoJsonTest {
    private final JacksonTester<NewBookingDto> json;

    @SneakyThrows
    @Test
    void fromNewBookingDtoTest() {
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        JsonContent<NewBookingDto> result = json.write(newBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-01T13:00:00");
    }

    @SneakyThrows
    @Test
    void toNewBookingDto() {
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );
        String jsonString = "{\"id\":0,\"itemId\":0,\"start\":\"2024-01-01T12:00:00\",\"end\":\"2024-01-01T13:00:00\"}";

        NewBookingDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), newBookingDto.getId());
        assertEquals(fromJson.getItemId(), newBookingDto.getItemId());
        assertEquals(fromJson.getStart(), newBookingDto.getStart());
        assertEquals(fromJson.getEnd(), newBookingDto.getEnd());
    }
}
