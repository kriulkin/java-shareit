package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BooingServiceIntTest {
    private final BookingServiceImpl bookingService;
    private final EntityManager em;

    @Test
    void addTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        em.persist(item1);
        NewBookingDto newBookingDto1 = new NewBookingDto(
                user1.getId(),
                item1.getId(),
                LocalDateTime.parse("2024-01-01T12:00:01"),
                LocalDateTime.parse("2024-01-01T13:00:01")
        );

        bookingService.add(user2.getId(), newBookingDto1);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        Booking booking = query.setParameter("item", item1).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem().getId(), equalTo(newBookingDto1.getItemId()));
        assertThat(booking.getBooker(), equalTo(user2));
        assertThat(booking.getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(booking.getEnd(), equalTo(newBookingDto1.getEnd()));
    }

    @Test
    void getTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        em.persist(item1);
        NewBookingDto newBookingDto1 = new NewBookingDto(
                user1.getId(),
                item1.getId(),
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00")
        );

        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        em.persist(booking1);

        BookingDto bookingDto = bookingService.get(user1.getId(), booking1.getId());

        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(user1.getId()));
        assertThat(bookingDto.getStart(), equalTo(newBookingDto1.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(newBookingDto1.getEnd()));
    }

    @Test
    void updateStatusTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        em.persist(item1);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.WAITING
        );
        em.persist(booking1);

        bookingService.updateStatus(user2.getId(), booking1.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        Booking booking = query.setParameter("item", item1).getSingleResult();

        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void getByOwnerTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        em.persist(item1);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                item1,
                user1,
                Status.REJECTED
        );
        em.persist(booking1);
        em.persist(booking2);

        List<BookingDto> bookings = bookingService.getByOwnerId(user1.getId(), "REJECTED", 0, 25);

        TypedQuery<Booking> query = em.createQuery(
                "Select b from Booking b join b.item i where i.user = :user and b.status = :status", Booking.class);
        List<Booking> result = query.setParameter("user", user2).setParameter("status", Status.REJECTED).getResultList();

        assertThat(result, hasSize(1));
        assertThat(result, contains(booking2));
    }

    @Test
    void getByBookerIdTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item1 = new Item(null, "table", "Ivan's table", true, user2);
        em.persist(item1);
        Booking booking1 = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item1,
                user1,
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                LocalDateTime.parse("2024-01-01T12:00:00"),
                LocalDateTime.parse("2024-01-01T13:00:00"),
                item1,
                user1,
                Status.REJECTED
        );
        em.persist(booking1);
        em.persist(booking2);

        List<BookingDto> bookings = bookingService.getByBookerId(user1.getId(), "REJECTED", 0, 25);

        TypedQuery<Booking> query = em.createQuery(
                "Select b from Booking b where b.booker = :user and b.status = :status", Booking.class);
        List<Booking> result = query.setParameter("user", user1).setParameter("status", Status.APPROVED).getResultList();

        assertThat(result, hasSize(1));
        assertThat(result, contains(booking1));
    }
}
