package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addValidBookingTest() {
        User owner = new User(0L, "Ivan", "ivan@mail.com");
        User user = new User(1L, "Boris", "boris@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, owner, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                item.getId(),
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);
        when(userService.findById(anyLong())).thenReturn(user);
        when(itemService.findById(anyLong())).thenReturn(item);
        when(bookingStorage.save(any())).thenReturn(booking);

        BookingDto expectedBooking = bookingService.add(user.getId(), newBookingDto);

        Assertions.assertEquals(booking.getItem().getId(), expectedBooking.getId());
        Assertions.assertEquals(booking.getStart(), expectedBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), expectedBooking.getEnd());
        verify(bookingStorage, times(1)).save(BookingMapper.toBooking(newBookingDto, user, item));
    }

    @Test
    void addBookingWithUserOwnedItemTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                item.getId(),
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);
        when(userService.findById(anyLong())).thenReturn(user);
        when(itemService.findById(anyLong())).thenReturn(item);

        Assertions.assertThrows(NoSuchEntityException.class, () -> bookingService.add(user.getId(), newBookingDto));
        verify(bookingStorage, times(0)).save(any());
    }

    @Test
    void addBookingForNotAvailableItemTest() {
        User owner = new User(0L, "Ivan", "ivan@mail.com");
        User user = new User(1L, "Boris", "boris@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", false, owner, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                item.getId(),
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);
        when(userService.findById(anyLong())).thenReturn(user);
        when(itemService.findById(anyLong())).thenReturn(item);

        Assertions.assertThrows(NotAvailableException.class, () -> bookingService.add(user.getId(), newBookingDto));
        verify(bookingStorage, times(0)).save(any());
    }

    @Test
    void addBookingWithNoItemTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemService.findById(anyLong())).thenThrow(NoSuchEntityException.class);

        Assertions.assertThrows(NoSuchEntityException.class, () -> bookingService.add(user.getId(), newBookingDto));
        verify(bookingStorage, times(0)).save(any());
    }

    @Test
    void getExistedBooking() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                0L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);

        when(userService.findById(anyLong())).thenReturn(user);
        when(bookingStorage.findById(any())).thenReturn(Optional.of(booking));

        BookingDto expectedBooking = bookingService.get(user.getId(), item.getId());

        Assertions.assertEquals(booking.getItem().getId(), expectedBooking.getId());
        Assertions.assertEquals(booking.getStart(), expectedBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), expectedBooking.getEnd());
        verify(bookingStorage, times(1)).findById(anyLong());
    }

    @Test
    void getNonExistedBookingTest() {
        when(bookingStorage.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchEntityException.class, () -> bookingService.get(0L, 0L));
    }

    @Test
    void getBookingWithUserNotHisItemAndBookingTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        User notValidUser = new User(5L, "Boris", "boris@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                1L,
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);

        when(userService.findById(anyLong())).thenReturn(notValidUser);
        when(bookingStorage.findById(any())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NoSuchEntityException.class, () -> bookingService.get(notValidUser.getId(), 0L));
    }

    @Test
    void updateExistedBookingStatusTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                item.getId(),
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);

        when(userService.findById(anyLong())).thenReturn(user);
        when(bookingStorage.findById(any())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        BookingDto expectedBooking = bookingService.updateStatus(user.getId(), booking.getId(), true);

        Assertions.assertEquals(expectedBooking.getStatus(), Status.APPROVED);
        verify(bookingStorage, times(1)).save(any());
    }

    @Test
    void updateNonExistedBookingStatusTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");

        when(bookingStorage.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchEntityException.class, () -> bookingService.updateStatus(0L, 0L, true));
    }

    @Test
    void updateExistedBookingStatusTestWithReject() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewBookingDto newBookingDto = new NewBookingDto(
                0L,
                item.getId(),
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00")
        );

        Booking booking = BookingMapper.toBooking(newBookingDto, user, item);

        when(userService.findById(anyLong())).thenReturn(user);
        when(bookingStorage.findById(any())).thenReturn(Optional.of(booking));
        when(bookingStorage.save(any())).thenReturn(booking);

        BookingDto expectedBooking = bookingService.updateStatus(user.getId(), booking.getId(), false);

        Assertions.assertEquals(expectedBooking.getStatus(), Status.REJECTED);
        verify(bookingStorage, times(1)).save(any());
    }

    @Test
    void getByBookerIdStatusAllTest() {
        when(bookingStorage.findByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(Page.empty());

        bookingService.getByBookerId(0, "ALL", 0, 25);

        verify(bookingStorage, times(1))
                .findByBookerIdOrderByStartDesc(0, PageRequest.of(0, 25, Sort.Direction.DESC, "start"));
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByBookerIdStatusCurrentTest() {
        when(bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any())).thenReturn(Page.empty());

        bookingService.getByBookerId(0, "CURRENT", 0, 25);

        verify(bookingStorage, times(1))
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyLong(),
                        any(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByBookerIdStatusPastTest() {
        when(bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getByBookerId(0, "PAST", 0, 25);

        verify(bookingStorage, times(1))
                .findByBookerIdAndEndBeforeOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByBookerIdStatusFutureTest() {
        when(bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getByBookerId(0, "FUTURE", 0, 25);

        verify(bookingStorage, times(1))
                .findByBookerIdAndStartAfterOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByBookerIdStatusWaitingTest() {
        when(bookingStorage.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getByBookerId(0, "WAITING", 0, 25);

        verify(bookingStorage, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByBookerIdStatusRejectedTest() {
        when(bookingStorage.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any())).thenReturn(Page.empty());

        bookingService.getByBookerId(0, "REJECTED", 0, 25);

        verify(bookingStorage, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(
                        anyLong(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByOwnerIdStatusAllTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");

        when(userService.findById(anyLong())).thenReturn(user);
        when(bookingStorage.findByItemUserOrderByStartDesc(any(), any())).thenReturn(Page.empty());

        bookingService.getByOwnerId(0L, "ALL", 0, 25);

        verify(bookingStorage, times(1))
                .findByItemUserOrderByStartDesc(user, PageRequest.of(0, 25, Sort.Direction.DESC, "start"));
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByOwnerIdStatusCurrentTest() {
        when(bookingStorage.findByItemUserAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any())).thenReturn(Page.empty());

        bookingService.getByOwnerId(0, "CURRENT", 0, 25);

        verify(bookingStorage, times(1))
                .findByItemUserAndStartBeforeAndEndAfterOrderByStartDesc(
                        any(),
                        any(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByOwnerIdStatusPastTest() {
        when(bookingStorage.findByItemUserAndEndBeforeOrderByStartDesc(any(), any(), any())).thenReturn(Page.empty());

        bookingService.getByOwnerId(0, "PAST", 0, 25);

        verify(bookingStorage, times(1))
                .findByItemUserAndEndBeforeOrderByStartDesc(
                        any(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByOwnerIdStatusFutureTest() {
        when(bookingStorage.findByItemUserAndStartAfterOrderByStartDesc(any(), any(), any())).thenReturn(Page.empty());

        bookingService.getByOwnerId(0, "FUTURE", 0, 25);

        verify(bookingStorage, times(1))
                .findByItemUserAndStartAfterOrderByStartDesc(
                        any(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByUserIdStatusWaitingTest() {
        when(bookingStorage.findByItemUserAndStatusOrderByStartDesc(any(), any(), any())).thenReturn(Page.empty());

        bookingService.getByOwnerId(0, "WAITING", 0, 25);

        verify(bookingStorage, times(1))
                .findByItemUserAndStatusOrderByStartDesc(
                        any(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByUserIdStatusRejectedTest() {
        when(bookingStorage.findByItemUserAndStatusOrderByStartDesc(any(), any(), any())).thenReturn(Page.empty());

        bookingService.getByOwnerId(0, "REJECTED", 0, 25);

        verify(bookingStorage, times(1))
                .findByItemUserAndStatusOrderByStartDesc(
                        any(),
                        any(),
                        any()
                );
        verifyNoMoreInteractions(bookingStorage);
    }
}
