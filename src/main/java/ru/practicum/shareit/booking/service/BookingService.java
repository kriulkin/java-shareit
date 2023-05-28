package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(long userId, NewBookingDto newBookingDto);

    BookingDto updateStatus(Long userId, long bookingId, boolean approved);

    BookingDto get(long userId, long bookingId);

    List<BookingDto> getByBookerId(long userId, String state);

    List<BookingDto> getByOwnerId(long userId, String state);
}
