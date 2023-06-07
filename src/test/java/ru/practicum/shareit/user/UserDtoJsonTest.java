package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void fromUserDtoTest() {
        UserDto userDto = new UserDto(0L, "Ivan", "ivan@mail.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(0);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@mail.com");
    }

    @SneakyThrows
    @Test
    void toUserDtoTest() {
        String jsonString = "{\"id\":0,\"name\":\"Ivan\",\"email\":\"ivan@mail.com\"}";

        UserDto userDto = new UserDto(0L, "Ivan", "ivan@mail.com");
        UserDto fromJson = json.parseObject(jsonString);

        assertEquals(fromJson.getId(), userDto.getId());
        assertEquals(fromJson.getName(), userDto.getName());
        assertEquals(fromJson.getEmail(), userDto.getEmail());
    }
}
