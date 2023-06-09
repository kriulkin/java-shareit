package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AnsweredItemRequestDtoJsonTest {
    private final JacksonTester<AnsweredItemRequestDto> json;

    @SneakyThrows
    @Test
    void fromAnsweredItemRequestDtoTest() {
        AnsweredItemRequestDto requestDto = new AnsweredItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00"),
                Collections.emptyList()
        );

        JsonContent<AnsweredItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("table");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T12:00:00");
    }

    @SneakyThrows
    @Test
    void toAnsweredItemRequestDtoTest() {
        AnsweredItemRequestDto requestDto = new AnsweredItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00"),
                Collections.emptyList()
        );

        String jsonString = "{\"id\":0,\"description\":\"table\",\"created\":\"2023-01-01T12:00:00\"}";

        AnsweredItemRequestDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), requestDto.getId());
        assertEquals(fromJson.getDescription(), requestDto.getDescription());
        assertEquals(fromJson.getCreated(), requestDto.getCreated());
    }
}
