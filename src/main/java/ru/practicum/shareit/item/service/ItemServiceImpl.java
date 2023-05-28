package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public Item findById(long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("Item with id = %d  doesn't exist", itemId)));
    }

    @Override
    public ItemBookingDto get(long userId, long itemId) {
        User user = userService.findById(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("Item with id = %d  doesn't exist", itemId)));

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getUser().equals(user)) {
            List<Booking> bookings = bookingStorage.findByItemAndItemUserAndStatusOrderByStartAsc(
                    item, user, Status.APPROVED);
            lastBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
        }

        List<Comment> comments = commentStorage.findByItemOrderById(item);

        return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, comments);
    }


    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(userService.findById(userId), itemDto)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        User user = userService.findById(userId);

        Item item = itemStorage.findById(itemDto.getId())
                .orElseThrow(() -> new NoSuchEntityException(String.format("Item with id = %d  doesn't exist", itemDto.getId())));

        if (userId != item.getUser().getId()) {
            throw new NoSuchEntityException(
                    String.format("User with id = %d  doesn't have item with id = %d", userId, item.getId())
            );
        }

        Item updatedItem = ItemMapper.toItem(user, itemDto);

        if (updatedItem.getName() == null || updatedItem.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }

        if (updatedItem.getDescription() == null || updatedItem.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }

        if (updatedItem.getAvailable() == null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.save(updatedItem));

    }

    @Override
    public List<ItemBookingDto> findByUserId(long userId) {
        User user = UserMapper.toUser(userService.get(userId));
        List<Item> items = itemStorage.findByUser(user);
        List<ItemBookingDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            List<Booking> bookings = bookingStorage.findByItemAndItemUserAndStatusOrderByStartAsc(
                    item, user, Status.APPROVED);
            Booking lastBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            Booking nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            itemDtos.add(ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, commentStorage.findByItemOrderById(item)));
        }

        return itemDtos;
    }

    @Override
    public List<ItemDto> search(String term) {
        if (term == null || term.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.search(term).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userService.findById(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NoSuchEntityException(String.format("Item with id = %d  doesn't exist", itemId)));

        if (!bookingStorage.existsByItemAndBookerAndStatusAndEndBefore(item, user, Status.APPROVED, LocalDateTime.now())) {
            throw new NotAvailableException(
                    String.format("User with id = %d  hadn't booked item with id = %d", userId, itemId)
            );
        }

        if (commentStorage.existsByItemAndAuthor(item, user)) {
            throw new NotAvailableException(
                    String.format("User with id = %d already has posted item with id = %d", userId, itemId)
            );
        }

        Comment comment = CommentMapper.toComment(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentStorage.save(comment));

    }
}
