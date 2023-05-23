package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated @RequestBody NewBookingDto newbookingDto) {
        log.info("Пользователь с id = {} пытается забронировать вещь с id = {}", userId, newbookingDto.getItemId());

        return bookingService.add(userId, newbookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        log.info("Пользователь с id = {} пытается обновить статус бронирования id = {}", userId, bookingId);

        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long bookingId) {
        log.info("Пользователь с id = {} пытается получить броинрование с id = {}", userId, bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Пользователь с id = {} пытается получить список своих броинрований с состоянием = {}",
                userId, state);
        return bookingService.getByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Пользователь с id = {} пытается получить список своих броинрований с состоянием = {}",
                userId, state);
        return bookingService.getByOwnerId(userId, state);
    }
}
