package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void fromItemRequestDtoTest() {
        ItemRequestDto requestDto = new ItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("table");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T12:00:00");
    }

    @SneakyThrows
    @Test
    void toItemRequestDtoTest() {
        ItemRequestDto requestDto = new ItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        String jsonString = "{\"id\":0,\"description\":\"table\",\"created\":\"2023-01-01T12:00:00\"}";

        ItemRequestDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), requestDto.getId());
        assertEquals(fromJson.getDescription(), requestDto.getDescription());
        assertEquals(fromJson.getCreated(), requestDto.getCreated());
    }
}
