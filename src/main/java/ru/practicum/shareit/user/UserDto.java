package ru.practicum.shareit.user;

import lombok.*;
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
    String Name;
    @EqualsAndHashCode.Include
    @Email(message = "Пользователь с невалидным Email", groups = {New.class, UpdateFields.class})
    @NotNull(message = "Пользователь с пустым Email", groups = New.class)
    String email;

    public interface New {}

    public interface Exist {}

    public interface UpdateFields extends Exist {}
}
