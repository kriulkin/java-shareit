package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
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
public class ItemServiceIntTest {
    public final ItemServiceImpl itemService;
    public final UserServiceImpl userService;
    private final EntityManager em;

    @Test
    void addTest() {
        ItemDto itemDto = new ItemDto(0L, "table", "Ivan's table", true, null);
        User user = new User(null, "Ivan", "ivan@email.com");
        em.persist(user);

        itemService.add(user.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getRequest(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void getTest() {
        User user = new User(null, "Ivan", "ivan@email.com");
        em.persist(user);

        Item item = new Item(null, "table", "Ivan's table", true, user);
        em.persist(item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i", Item.class);
        ItemBookingDto itemDto = itemService.get(user.getId(), query.getSingleResult().getId());

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void findByUserIdTest() {
        User user1 = new User(null, "Ivan", "ivan@email.com");
        User user2 = new User(null, "Boris", "boris@email.com");
        em.persist(user1);
        em.persist(user2);

        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        em.persist(item1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item2);
        Item item3 = new Item(null, "bag", "Boris's bag", true, user2);
        em.persist(item3);

        List<Item> items = List.of(item1, item2);

        List<ItemBookingDto> ivansItems = itemService.findByUserId(user1.getId(), 0, 20);

        assertThat(ivansItems, hasSize(2));
        for (Item item : items) {
            assertThat(ivansItems, hasItem((allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            ))));
        }
    }

    @Test
    void searchTest() {
        User user1 = new User(null, "Ivan", "ivan@email.com");
        em.persist(user1);
        Item item1 = new Item(null, "table", "Ivan's table", true, user1);
        em.persist(item1);
        Item item2 = new Item(null, "pencil", "Ivan's pencil", true, user1);
        em.persist(item2);

        List<Item> items = List.of(item2);

        List<ItemDto> foundItems = itemService.search("pencil", 0, 20);

        assertThat(foundItems, hasSize(1));
        for (Item item : items) {
            assertThat(foundItems, hasItem((allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            ))));
        }
    }

    @Test
    void addCommentTest() {
        User user = new User(null, "Ivan", "ivan@email.com");
        em.persist(user);
        Item item = new Item(null, "table", "Ivan's table", true, user);
        em.persist(item);
        Booking booking = new Booking(
                LocalDateTime.parse("2023-01-01T12:00:00"),
                LocalDateTime.parse("2023-01-01T13:00:00"),
                item,
                user,
                Status.APPROVED
        );
        em.persist(booking);
        CommentDto commentDto = new CommentDto(
                0L,
                "Nice table",
                user.getName(),
                LocalDateTime.parse("2023-01-01T12:00:00")
        );


        itemService.addComment(user.getId(), item.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c", Comment.class);
        Comment fetchedComment = query.getSingleResult();

        assertThat(fetchedComment.getId(), notNullValue());
        assertThat(fetchedComment.getText(), equalTo(commentDto.getText()));
        assertThat(fetchedComment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
    }
}
