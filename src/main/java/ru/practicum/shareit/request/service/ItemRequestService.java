package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(long userId, NewItemRequestDto itemRequestDto);
    
    List<AnsweredItemRequestDto> findByUserId(long userId);

    List<AnsweredItemRequestDto> findAllButNotUserId(long userId, int from, int size);

    AnsweredItemRequestDto get(long userId, long itemRequestId);
}
