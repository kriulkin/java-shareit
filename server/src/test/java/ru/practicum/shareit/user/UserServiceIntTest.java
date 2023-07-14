package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceIntTest {
    public final UserServiceImpl service;
    private final EntityManager em;

    @Test
    void saveTest() {
        UserDto userDto = new UserDto(0L, "Ivan", "ivan@email.com");
        service.add(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getTest() {
        User user = makeUser("Ivan", "ivan@mail.com");
        em.persist(user);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        User fetchedUser = UserMapper.toUser(service.get(query.getSingleResult().getId()));

        assertThat(fetchedUser.getName(), equalTo("Ivan"));
        assertThat(fetchedUser.getEmail(), equalTo("ivan@mail.com"));
    }

    @Test
    void findAllWithNoUserTest() {
        List<User> users = service.findAll();

        assertEquals(users.size(), 0);
    }

    @Test
    void getAllUsers() {
        List<User> sourceUsers = List.of(
                makeUser("Ivan", "ivan@email"),
                makeUser("Petr", "petr@email"),
                makeUser("Boris", "boris@email")
        );

        for (User user : sourceUsers) {
            em.persist(user);
        }
        em.flush();

        // when
        List<User> targetUsers = service.findAll();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (User sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void deleteTest() {
        User user = makeUser("Ivan", "ivan@mail.com");
        em.persist(user);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        service.delete(query.getSingleResult().getId());

        assertThrows(NoResultException.class, query::getSingleResult);
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
