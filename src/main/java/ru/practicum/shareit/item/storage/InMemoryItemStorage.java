package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private long currentId;
    private final Map<Long, Set<Item>> items = new HashMap<>();

    @Override
    public Item get(long id) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Item> findByUserId(long userId) {
        return List.copyOf(items.getOrDefault(userId, Collections.emptySet()));
    }

    @Override
    public Item add(Item item) {
        item.setId(getCurrentId());
        items.compute(item.getUserId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new HashSet<>();
            }
            userItems.add(item);
            return userItems;
        });

        return item;
    }

    @Override
    public Item update(Item item) {
        Item currentItem = get(item.getId());

        if (currentItem == null) {
            return null;
        }

        if (item.getName() != null) {
            currentItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            currentItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }

        return currentItem;
    }

    @Override
    public List<Item> search(String term) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase().contains(term.toLowerCase())
                        || item.getDescription().toLowerCase().contains(term.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private long getCurrentId() {
        return ++currentId;
    }
}
