package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserStorage storage;

    User user = new User(0L, "Ivan", "ivan@mail.com");
    UserDto userDto = new UserDto(0L, "Ivan", "ivan@mail.com");

    @Test
    void findAllTest() {
        when(storage.findAll()).thenReturn(List.of());

        List<User> users = service.findAll();

        verify(storage, times(1)).findAll();
        assertEquals(users.size(), 0);
    }

    @Test
    void addValidUserTest() {
        when(storage.save(Mockito.any(User.class))).thenReturn(user);

        service.add(userDto);

        verify(storage, times(1)).save(user);
    }

    @Test
    void getExistedUserTest() {
        when(storage.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        service.get(0L);

        verify(storage, times(1)).findById(0L);
    }

    @Test
    void getNonExistedUserTest() {
        when(storage.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> service.get(0L), "User with id = 0 doesn't exist");
    }

    @Test
    void deleteExistedUser() {
        when(storage.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        service.delete(0L);

        verify(storage, times(1)).delete(user);
    }

    @Test
    void deleteNonExistedUser() {
        when(storage.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> service.delete(0L), "User with id = 0 doesn't exist");
        verifyNoMoreInteractions(storage);
    }
}
