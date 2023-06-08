package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void fromCommentDtoTest() {
        CommentDto commentDto = new CommentDto(
                0L,
                "comment",
                "Ivan",
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T12:00:00");
    }

    @SneakyThrows
    @Test
    void toCommentDtoTest() {
        CommentDto commentDto = new CommentDto(
                0L,
                "comment",
                "Ivan",
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        String jsonString = "{\"id\":0,\"text\":\"comment\",\"authorName\":\"Ivan\",\"created\":\"2023-01-01T12:00:00\"}";

        CommentDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), commentDto.getId());
        assertEquals(fromJson.getAuthorName(), commentDto.getAuthorName());
        assertEquals(fromJson.getText(), commentDto.getText());
        assertEquals(fromJson.getCreated(), commentDto.getCreated());
    }
}

