package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item get(long id);

    List<Item> findByUserId(long userId);

    Item add(Item item);

    Item update(Item item);

    List<Item> search(String term);
}
