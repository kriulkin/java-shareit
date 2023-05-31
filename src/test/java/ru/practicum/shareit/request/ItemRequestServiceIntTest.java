package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemRequestServiceIntTest {
    private final ItemRequestServiceImpl requestService;
    private final EntityManager em;

    @Test
    void findAllButNotUserIdTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item = new Item(null, "table", "Ivan's table", true, user1, null);
        em.persist(item);
        ItemRequest request1 = new ItemRequest(
                null,
                "table",
                user1,
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        ItemRequest request2 = new ItemRequest(
                null,
                "table",
                user1,
                LocalDateTime.parse("2024-01-01T12:00:00")
        );
        ItemRequest request3 = new ItemRequest(
                null,
                "table",
                user2,
                LocalDateTime.parse("2024-01-01T12:00:00")
        );
        em.persist(request1);
        em.persist(request2);
        em.persist(request3);

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select r from ItemRequest r where r.requestor != :user order by r.created desc", ItemRequest.class);
        List<ItemRequest> result = query.setParameter("user", user2).getResultList();

        List<AnsweredItemRequestDto> requests = requestService.findAllButNotUserId(user2.getId(), 0, 25);
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(result.get(1).getId(), equalTo(requests.get(1).getId()));
    }

    @Test
    void findByUserIdTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
        Item item = new Item(null, "table", "Ivan's table", true, user1, null);
        em.persist(item);
        ItemRequest request1 = new ItemRequest(
                null,
                "table",
                user1,
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        ItemRequest request2 = new ItemRequest(
                null,
                "table",
                user1,
                LocalDateTime.parse("2024-01-01T12:00:00")
        );
        ItemRequest request3 = new ItemRequest(
                null,
                "table",
                user2,
                LocalDateTime.parse("2024-01-01T12:00:00")
        );
        em.persist(request1);
        em.persist(request2);
        em.persist(request3);

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select r from ItemRequest r where r.requestor = :user order by r.created desc", ItemRequest.class);
        List<ItemRequest> result = query.setParameter("user", user1).getResultList();

        List<AnsweredItemRequestDto> requests = requestService.findByUserId(user1.getId());
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(result.get(1).getId(), equalTo(requests.get(1).getId()));
    }
}
