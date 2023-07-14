package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item findById(long itemId);

    ItemBookingDto get(long userId, long itemId);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    List<ItemBookingDto> findByUserId(long userId, int from, int size);

    List<ItemDto> search(String term, int from, int size);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
