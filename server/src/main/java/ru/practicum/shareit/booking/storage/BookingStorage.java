package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface BookingStorage extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable page);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long userId,
            LocalDateTime after,
            LocalDateTime before,
            Pageable page);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime before, Pageable page);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime after, Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, Status status, Pageable page);

    Page<Booking> findByItemUserOrderByStartDesc(User user, Pageable page);

    Page<Booking> findByItemUserAndStartBeforeAndEndAfterOrderByStartDesc(
            User user,
            LocalDateTime after,
            LocalDateTime before,
            Pageable page);

    Page<Booking> findByItemUserAndEndBeforeOrderByStartDesc(User user, LocalDateTime before, Pageable page);

    Page<Booking> findByItemUserAndStartAfterOrderByStartDesc(User user, LocalDateTime after, Pageable page);

    Page<Booking> findByItemUserAndStatusOrderByStartDesc(User user, Status status, Pageable page);

    List<Booking> findByItemAndItemUserAndStatusOrderByStartAsc(Item item, User user, Status status);

    boolean existsByItemAndBookerAndStatusAndEndBefore(Item item, User user, Status status, LocalDateTime now);
}
