package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    long id;
    @NotBlank(message = "Item with empty name", groups = New.class)
    String name;
    @NotBlank(message = "Item with empty description", groups = New.class)
    String description;
    @NotNull(message = "Item with empty status", groups = New.class)
    Boolean available;

    public interface New {
    }

    public interface Exist {
    }

    public interface UpdateFields extends Exist {
    }
}
