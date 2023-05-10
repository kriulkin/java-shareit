package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto get(long itemId);

    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto);

    List<ItemDto> findByUserId(long userId);

    List<ItemDto> search(String term);
}
