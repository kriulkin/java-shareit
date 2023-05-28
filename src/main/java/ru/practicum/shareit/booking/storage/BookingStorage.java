package ru.practicum.shareit.booking.storage;

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
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long userId,
            LocalDateTime after,
            LocalDateTime before
    );

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime before);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime after);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findByItemUserOrderByStartDesc(User user);

    List<Booking> findByItemUserAndStartBeforeAndEndAfterOrderByStartDesc(
            User user,
            LocalDateTime after,
            LocalDateTime before
    );

    List<Booking> findByItemUserAndEndBeforeOrderByStartDesc(User user, LocalDateTime before);

    List<Booking> findByItemUserAndStartAfterOrderByStartDesc(User user, LocalDateTime after);

    List<Booking> findByItemUserAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findByItemAndItemUserAndStatusOrderByStartAsc(Item item, User user, Status status);

    boolean existsByItemAndBookerAndStatusAndEndBefore(Item item, User user, Status status, LocalDateTime now);
}
