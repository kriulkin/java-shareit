package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated @RequestBody NewBookingDto newBookingDto) {
        log.info("User with id = {} trying to book item with id = {}", userId, newBookingDto.getItemId());

        return bookingService.add(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        log.info("User with id = {} trying to update status of of booking with id = {}}", userId, bookingId);

        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long bookingId) {
        log.info("User with id = {} trying to fetch booking with id = {}", userId, bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "ALL", required = false) String state,
                                          @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "25", required = false) @Positive int size) {
        log.info("User with id = {} trying to fetch list of own bookings with status = {}",
                userId, state);
        return bookingService.getByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "ALL", required = false) String state,
                                         @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "25", required = false) @Positive int size) {
        log.info("User with id = {} trying to fetch list of bookings of his own items with status = {}",
                userId, state);
        return bookingService.getByOwnerId(userId, state, from, size);
    }
}
