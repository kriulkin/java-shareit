package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NewBookingDto {
    long id;

    @NotNull(message = "Бронирование с пустым идентификатором вещи")
    long itemId;

    @Future(message = "Бронирование с некрректной датой начала")
    @NotNull(message = "Бронирование с пустой датой начала")
    LocalDateTime start;

    @Future(message = "Бронирование с некрректной датой завершения")
    @NotNull(message = "Бронирование с пустой датой завершения")
    LocalDateTime end;
}
