package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
    Long id;
    String name;
    @EqualsAndHashCode.Include
    @Email(message = "User with incorrect email", groups = {New.class, UpdateFields.class})
    @NotNull(message = "User with empty email", groups = New.class)
    String email;

    public interface New {
    }

    public interface Exist {
    }

    public interface UpdateFields extends Exist {
    }
}
