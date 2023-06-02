package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserService userService;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private ItemRequestStorage requestStorage;

    @Test
    void getExistedItemTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        Booking lastBooking = new Booking(
                0L,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                item,
                user,
                Status.APPROVED
        );
        Booking nextBooking = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user,
                Status.APPROVED
        );

        ItemBookingDto expectedItemBookingDto = ItemMapper.toItemBookingDto(item,
                lastBooking,
                nextBooking,
                Collections.emptyList());

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentStorage.findByItemOrderById(any())).thenReturn(Collections.emptyList());
        when(bookingStorage.findByItemAndItemUserAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));

        ItemBookingDto itemBookingDto = itemService.get(0L, 0L);

        verify(itemStorage, times(1)).findById(0L);

        Assertions.assertEquals(itemBookingDto, expectedItemBookingDto);
    }

    @Test
    void getItemWithNoUserTest() {
        when(userService.findById(anyLong())).thenReturn(null);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> itemService.get(0L, 0L), "User with id = 0 doesn't exist");
    }

    @Test
    void getNonExistentItemTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> itemService.get(0L, 0L), "Item with id = 0 doesn't exist");
    }

    @Test
    void addItemWithoutRequestTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, null);

        Item item = ItemMapper.toItem(user, itemDto, null);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.save(any())).thenReturn(item);

        ItemDto expectedItemDto = itemService.add(user.getId(), itemDto);

        verify(itemStorage, times(1)).save(item);
        Assertions.assertEquals(itemDto, expectedItemDto);
    }

    @Test
    void addItemWithRequestTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        ItemDto itemDto = new ItemDto(0L, "table", "Ivans table", true, 0L);
        ItemRequest request = new ItemRequest(0L,
                "Request",
                user,
                LocalDateTime.now().minusHours(3)
        );


        Item item = ItemMapper.toItem(user, itemDto, request);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.save(any())).thenReturn(item);
        when(requestStorage.getById(anyLong())).thenReturn(request);

        ItemDto expectedItemDto = itemService.add(user.getId(), itemDto);

        verify(itemStorage, times(1)).save(item);
        Assertions.assertEquals(itemDto, expectedItemDto);
    }

    @Test
    void updateValidItemWithNameTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        ItemDto itemDto = new ItemDto(0L, "kitchen table", "Ivan's table", true, null);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemStorage.save(any())).thenReturn(item);

        ItemDto expectedItemDto = itemService.update(user.getId(), itemDto);

        verify(itemStorage, times(1)).save(item);
        Assertions.assertEquals(itemDto.getName(), expectedItemDto.getName());
    }

    @Test
    void updateValidItemWithDescriptionTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        ItemDto itemDto = new ItemDto(0L, "table", "Petr's table", true, null);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemStorage.save(any())).thenReturn(item);

        ItemDto expectedItemDto = itemService.update(user.getId(), itemDto);

        verify(itemStorage, times(1)).save(item);
        Assertions.assertEquals(itemDto.getDescription(), expectedItemDto.getDescription());
    }

    @Test
    void updateNonExistedItemTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        ItemDto itemDto = new ItemDto(0L, "table", "Petr's table", true, null);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        verify(itemStorage, times(0)).save(any());
        Assertions.assertThrows(NoSuchEntityException.class,
                () -> itemService.update(user.getId(), itemDto),
                "Item with id = 0  doesn't exist");
    }

    @Test
    void searchTest() {
        when(itemStorage.search(anyString(), any()))
                .thenReturn(Page.empty());

        itemService.search("text", 0, 25);

        verify(itemStorage, times(1)).search("text",
                PageRequest.of(0 / 25, 25));
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void addValidCommentTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        CommentDto commentDto = new CommentDto(0L, "Nice table", user.getName(), LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, user, item);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.existsByItemAndBookerAndStatusAndEndBefore(any(), any(), any(), any())).thenReturn(true);
        when(commentStorage.existsByItemAndAuthor(any(), any())).thenReturn(false);
        when(commentStorage.save(any())).thenReturn(comment);

        CommentDto expectedCommentDto = itemService.addComment(user.getId(), item.getId(), commentDto);
        comment.setCreated(commentDto.getCreated());
        verify(commentStorage, times(1)).save(any(Comment.class));
        Assertions.assertEquals(commentDto, expectedCommentDto);
    }

    @Test
    void addCommentWithoutBookingTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        CommentDto commentDto = new CommentDto(0L, "Nice table", user.getName(), LocalDateTime.now());

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.existsByItemAndBookerAndStatusAndEndBefore(any(), any(), any(), any())).thenReturn(false);

        Assertions.assertThrows(NotAvailableException.class,
                () -> itemService.addComment(0L, item.getId(), commentDto),
                "User with id = 0 hadn't booked item with id = 0");
        verify(commentStorage, times(0)).save(any(Comment.class));
    }

    @Test
    void addSecondCommentTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        CommentDto commentDto = new CommentDto(0L, "Nice table", user.getName(), LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, user, item);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.existsByItemAndBookerAndStatusAndEndBefore(any(), any(), any(), any())).thenReturn(true);
        when(commentStorage.existsByItemAndAuthor(any(), any())).thenReturn(true);

        Assertions.assertThrows(NotAvailableException.class,
                () -> itemService.addComment(0L, item.getId(), commentDto),
                "User with id = 0 already has posted item with id = 0");
        verify(commentStorage, times(0)).save(any(Comment.class));
    }
}
