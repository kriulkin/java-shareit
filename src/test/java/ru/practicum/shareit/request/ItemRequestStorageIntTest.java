package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestStorageIntTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRequestStorage requestStorage;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findByRequestorOrderByCreatedDescTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
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

        List<ItemRequest> result = requestStorage.findByRequestorOrderByCreatedDesc(user1);

        assertThat(result)
                .asList()
                .hasSize(2)
                .containsAll(List.of(request1, request2))
                .first().isEqualTo(request2);
    }

    @Test
    void findByRequestorNotTest() {
        User user1 = new User(null, "Ivan", "ivan@mail.com");
        User user2 = new User(null, "Petr", "petr@mail.com");
        em.persist(user1);
        em.persist(user2);
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

        Page<ItemRequest> result = requestStorage.findByRequestorNot(user2, PageRequest.of(0, 25, Sort.Direction.ASC, "id"));

        assertThat(result.getContent())
                .asList()
                .hasSize(2)
                .containsAll(List.of(request1, request2))
                .first().isEqualTo(request1);
    }
}
