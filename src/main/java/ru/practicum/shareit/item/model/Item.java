package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
public class Item {
    long id;
    String name;
    String description;
    Boolean available;
    long userId;
}
