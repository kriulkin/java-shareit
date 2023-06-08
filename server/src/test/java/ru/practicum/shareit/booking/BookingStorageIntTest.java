package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingStorageIntTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    BookingStorage bookingStorage;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findByBookerIdOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByBookerIdOrderByStartDesc(
                user1.getId(),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(2)
                .containsAll(List.of(booking1, booking2))
                .first().isEqualTo(booking1);
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user1.getId(),
                LocalDateTime.parse("2022-06-01T12:10:00"),
                LocalDateTime.parse("2022-06-01T12:15:00"),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking2))
                .first().isEqualTo(booking2);
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(
                user1.getId(),
                LocalDateTime.parse("2022-06-20T12:10:00"),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking2))
                .first().isEqualTo(booking2);
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(
                user1.getId(),
                LocalDateTime.parse("2022-06-20T12:10:00"),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .isNotNull()
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking1))
                .first().isEqualTo(booking1);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.REJECTED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(
                user1.getId(),
                Status.REJECTED,
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking2))
                .first().isEqualTo(booking2);
    }

    @Test
    void findByItemUserOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByItemUserOrderByStartDesc(
                user2,
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(2)
                .containsAll(List.of(booking1, booking3))
                .first().isEqualTo(booking1);
    }

    @Test
    void findByItemUserAndStartBeforeAndEndAfterOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByItemUserAndStartBeforeAndEndAfterOrderByStartDesc(
                user2,
                LocalDateTime.parse("2022-06-01T12:10:00"),
                LocalDateTime.parse("2022-06-01T12:15:00"),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking3))
                .first().isEqualTo(booking3);
    }

    @Test
    void findByItemUserAndEndBeforeOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByItemUserAndEndBeforeOrderByStartDesc(
                user2,
                LocalDateTime.parse("2022-06-20T12:10:00"),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking3))
                .first().isEqualTo(booking3);
    }

    @Test
    void findByItemUserAndStartAfterOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.APPROVED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByItemUserAndStartAfterOrderByStartDesc(
                user2,
                LocalDateTime.parse("2022-06-20T12:10:00"),
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking1))
                .first().isEqualTo(booking1);
    }

    @Test
    void findByItemUserAndStatusOrderByStartDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.CANCELED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.REJECTED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Page<Booking> result = bookingStorage.findByItemUserAndStatusOrderByStartDesc(
                user1,
                Status.CANCELED,
                PageRequest.of(0, 20, Sort.Direction.DESC, "start")
        );

        assertThat(result.getContent())
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking1))
                .first().isEqualTo(booking1);
    }

    @Test
    void findByItemAndItemUserAndStatusOrderByStartAscTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.CANCELED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.REJECTED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.APPROVED
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        List<Booking> result = bookingStorage.findByItemAndItemUserAndStatusOrderByStartAsc(
                item1,
                user1,
                Status.CANCELED
        );

        assertThat(result)
                .asList()
                .hasSize(1)
                .containsAll(List.of(booking1))
                .first().isEqualTo(booking1);
    }

    @Test
    void existsByItemAndBookerAndStatusAndEndBeforeTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item1);
        em.persist(item2);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.CANCELED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item2,
                user1,
                Status.REJECTED
        );
        Booking booking3 = new Booking(
                LocalDateTime.parse("2022-06-01T12:00:00"),
                LocalDateTime.parse("2022-06-01T13:00:00"),
                item1,
                user2,
                Status.WAITING
        );
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);

        Boolean result = bookingStorage.existsByItemAndBookerAndStatusAndEndBefore(
                item1,
                user2,
                Status.WAITING,
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        assertThat(result)
                .isEqualTo(true);
    }
}
