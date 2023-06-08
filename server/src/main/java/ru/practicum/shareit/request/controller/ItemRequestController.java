package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AnsweredItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    public final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated @RequestBody NewItemRequestDto itemRequestDto) {
        log.info("User with id = {} trying to add new item request", userId);
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping
    public List<AnsweredItemRequestDto> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("User with id = {} trying to fetch list of own item requests", userId);
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("/all")
    public List<AnsweredItemRequestDto> findAllButNotUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "25", required = false) @Positive int size) {
        log.info("User with id = {} trying to fetch list of item requests", userId);
        return itemRequestService.findAllButNotUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public AnsweredItemRequestDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long requestId) {
        log.info("User with id = {} trying to fetch item request with id = {} ", userId, requestId);
        return itemRequestService.get(userId, requestId);
    }
}
