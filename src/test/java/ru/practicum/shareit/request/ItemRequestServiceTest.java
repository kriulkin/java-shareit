package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    @Mock
    private ItemRequestStorage requestStorage;
    @Mock
    private UserService userService;
    @Mock
    ItemStorage itemStorage;

    @Test
    void addValidRequest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        ItemRequest request = ItemRequestMapper.toItemRequest(user, newItemRequestDto);
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        when(userService.findById(anyLong())).thenReturn(user);
        when(requestStorage.save(any())).thenReturn(request);

        ItemRequestDto result = requestService.add(user.getId(), newItemRequestDto);

        Assertions.assertEquals(result, requestDto);
    }

    @Test
    void getExistentRequestTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto(
                0L,
                "table",
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        ItemRequest request = ItemRequestMapper.toItemRequest(user, newItemRequestDto);
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        when(userService.findById(anyLong())).thenReturn(user);
        when(itemStorage.findByRequest(request)).thenReturn(List.of(item));
        when(requestStorage.findById(any())).thenReturn(Optional.of(request));

        AnsweredItemRequestDto result = requestService.get(user.getId(), request.getId());


        Assertions.assertEquals(result, ItemRequestMapper.toAnsweredItemRequestDto(request, List.of(item)));
    }

    @Test
    void getNonExistentRequestTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        when(userService.findById(anyLong())).thenReturn(user);
        when(requestStorage.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchEntityException.class, () -> requestService.get(0L, 0L));
    }

    @Test
    void findByUserIdTest() {
        User user = new User(0L, "Ivan", "ivan@mail.com");
        Item item = new Item(0L, "table", "Ivan's table", true, user, null);
        ItemRequest request1 = new ItemRequest(
                0L,
                "table",
                user,
                LocalDateTime.parse("2023-01-01T12:00:00")
        );

        ItemRequest request2 = new ItemRequest(
                1L,
                "table",
                user,
                LocalDateTime.parse("2024-01-01T12:00:00")
        );

        when(userService.findById(anyLong())).thenReturn(user);
        when(requestStorage.findByRequestorOrderByCreatedDesc(any())).thenReturn(List.of(request1, request2));

        List<AnsweredItemRequestDto> result = requestService.findByUserId(user.getId());

        Assertions.assertEquals(result.get(0).getId(), request1.getId());
        Assertions.assertEquals(result.get(0).getDescription(), request1.getDescription());
        Assertions.assertEquals(result.get(0).getCreated(), request1.getCreated());
        Assertions.assertEquals(result.get(1).getId(), request2.getId());
        Assertions.assertEquals(result.get(1).getDescription(), request2.getDescription());
        Assertions.assertEquals(result.get(1).getCreated(), request2.getCreated());
    }

    @Test
    void findByUserIdTestWithNonExistentUserTest() {
        when(userService.findById(anyLong())).thenThrow(NoSuchEntityException.class);

        Assertions.assertThrows(NoSuchEntityException.class, () -> requestService.findByUserId(0L));
    }
}
