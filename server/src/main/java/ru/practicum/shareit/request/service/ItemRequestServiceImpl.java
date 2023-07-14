package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;

    @Override
    public ItemRequestDto add(long userId, NewItemRequestDto itemRequestDto) {
        User user = userService.findById(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(user, itemRequestDto);

        return ItemRequestMapper.toItemRequestDto(itemRequestStorage.save(request));
    }

    @Override
    public List<AnsweredItemRequestDto> findByUserId(long userId) {
        User user = userService.findById(userId);

        List<ItemRequest> requests = itemRequestStorage.findByRequestorOrderByCreatedDesc(user);

        return requests.stream()
                .map(request -> ItemRequestMapper.toAnsweredItemRequestDto(request, itemStorage.findByRequest(request)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AnsweredItemRequestDto> findAllButNotUserId(long userId, int from, int size) {
        User user = userService.findById(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Page<ItemRequest> page = itemRequestStorage.findByRequestorNot(user, PageRequest.of(from / size, size, sort));

        return page.getContent().stream()
                .map(request -> ItemRequestMapper.toAnsweredItemRequestDto(request, itemStorage.findByRequest(request)))
                .collect(Collectors.toList());
    }

    @Override
    public AnsweredItemRequestDto get(long userId, long requestId) {
        User user = userService.findById(userId);
        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NoSuchEntityException(
                        String.format("Item request with id = %d doesn't exist", requestId)
                ));

        return ItemRequestMapper.toAnsweredItemRequestDto(request, itemStorage.findByRequest(request));
    }
}
