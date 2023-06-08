package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    Page<Item> findByUserOrderById(User user, Pageable page);

    @Query("select it " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%', :text, '%')) " +
            "or lower(it.description) like lower(concat('%', :text, '%'))) " +
            "and it.available = true ")
    Page<Item> search(String text, Pageable page);

    List<Item> findByRequest(ItemRequest request);
}
