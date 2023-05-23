package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDto add(long userId, NewBookingDto newBookingDto) {

        User user = userService.findById(userId);
        Item item = itemService.findById(newBookingDto.getItemId());

        if (item.getUser().equals(user)) {
            throw new NoSuchEntityException(
                    String.format("Пользователь с id = %d попытался забронировать свою вещь с id = %d",
                            userId, item.getId())
            );
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException(
                    String.format("Пользователь с id = %d попытался забронировать недоступную вещь с id = %d",
                            userId, item.getId())
            );
        }

        if (!newBookingDto.getStart().isBefore(newBookingDto.getEnd())) {
            throw new NotAvailableException("Дата начала бронирования должна быть реньше даты завершения");
        }

        Booking newBooking = bookingStorage.save(BookingMapper.toBooking(newBookingDto, user, item));
        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto updateStatus(Long userId, long bookingId, boolean approved) {

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("Бронирования с id %d не сущестувует", bookingId)));

        User user = userService.findById(userId);

        if (!booking.getItem().getUser().equals(user)) {
            throw new NoSuchEntityException(String.format("У пользователя с id = %d нет вещи с id = %d", userId, bookingId));
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new NotAvailableException(
                    String.format("Пользователь с id = %d попытался обновить статус для бронирования с id = %d",
                            userId, bookingId)
            );
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingStorage.save(booking));
    }

    @Override
    public BookingDto get(long userId, long bookingId) {

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("Бронирования с id %d не сущестувует", bookingId)));

        User user = userService.findById(userId);
        if (!booking.getItem().getUser().equals(user) && !booking.getBooker().equals(user)) {
            throw new NoSuchEntityException(String.format("Пользователь с id = %d не владеет и не бронирует вещь с id = %d",
                    userId, booking.getItem().getId()));
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByBookerId(long userId, String state) {
        User user = userService.findById(userId);
        List<Booking> bookings = new ArrayList<>();

        try {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookings = bookingStorage.findByBookerIdOrderByStartDesc(userId);
                    break;

                case CURRENT:
                    bookings = bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                    break;

                case PAST:
                    bookings = bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(
                            userId,
                            LocalDateTime.now()
                    );
                    break;

                case FUTURE:
                    bookings = bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(
                            userId,
                            LocalDateTime.now()
                    );
                    break;

                case WAITING:
                    bookings = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                    break;

                case REJECTED:
                    bookings = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new NotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwnerId(long userId, String state) {
        User user = userService.findById(userId);
        List<Booking> bookings = new ArrayList<>();

        try {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookings = bookingStorage.findByItemUserOrderByStartDesc(user);
                    break;

                case CURRENT:
                    bookings = bookingStorage.findByItemUserAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                    break;

                case PAST:
                    bookings = bookingStorage.findByItemUserAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now()
                    );
                    break;

                case FUTURE:
                    bookings = bookingStorage.findByItemUserAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now()
                    );
                    break;

                case WAITING:
                    bookings = bookingStorage.findByItemUserAndStatusOrderByStartDesc(user, Status.WAITING);
                    break;

                case REJECTED:
                    bookings = bookingStorage.findByItemUserAndStatusOrderByStartDesc(user, Status.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new NotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}

