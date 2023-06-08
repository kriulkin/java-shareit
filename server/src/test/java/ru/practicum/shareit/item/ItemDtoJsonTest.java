package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void fromItemDtoTest() {
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, 1L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("table");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Ivan's table");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @SneakyThrows
    @Test
    void toItemDtoTest() {
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, 1L);
        String jsonString = "{\"id\":0,\"name\":\"table\",\"description\":\"Ivan's table\",\"available\": true,\"requestId\":1}";

        ItemDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), itemDto.getId());
        assertEquals(fromJson.getName(), itemDto.getName());
        assertEquals(fromJson.getDescription(), itemDto.getDescription());
        assertEquals(fromJson.getAvailable(), itemDto.getAvailable());
        assertEquals(fromJson.getRequestId(), itemDto.getRequestId());
    }
}
