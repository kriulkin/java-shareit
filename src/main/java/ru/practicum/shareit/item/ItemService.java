package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto get(long itemId) {
        Item item = itemStorage.get(itemId);
        if (item == null) {
            throw new NoSuchEntityException(String.format("Вещь с id %d не сущестувует", itemId));
        }

        return ItemMapper.toItemDto(item);
    }

    public ItemDto add(long userId, ItemDto itemDto) {
        if (userStorage.get(userId) == null) {
            throw new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId));
        }

        return ItemMapper.toItemDto(itemStorage.add(ItemMapper.toItem(userId, itemDto)));
    }

    public ItemDto update(Long userId, ItemDto itemDto) {
        if (userStorage.get(userId) == null) {
            throw new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId));
        }

        Item item = itemStorage.get(itemDto.getId());
        if (userId != item.getUserId()) {
            throw new NoSuchEntityException(String.format("Вещь с id %d не сущестувует", userId));
        }

        Item updatedItem = itemStorage.update(ItemMapper.toItem(userId, itemDto));

        if (updatedItem == null) {
            throw new NoSuchEntityException(String.format("Вещь с id %d не сущестувует", itemDto.getId()));
        }

        return ItemMapper.toItemDto(updatedItem);

    }

    public List<ItemDto> findByUserId(long userId) {
        if (userStorage.get(userId) == null) {
            throw new NoSuchEntityException(String.format("Пользователь с id %d не сущестувует", userId));
        }

        return itemStorage.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String term) {
        if (term == null || term.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.search(term).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
