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

    @NotNull(message = "Booking with empty item id")
    long itemId;

    @Future(message = "Booking with incorrect start date")
    @NotNull(message = "Booking with empty start date")
    LocalDateTime start;

    @Future(message = "Booking with incorrect end date")
    @NotNull(message = "Booking with empty end date")
    LocalDateTime end;
}
