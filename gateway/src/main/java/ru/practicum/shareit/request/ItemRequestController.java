package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    public final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Validated @RequestBody NewItemRequestDto itemRequestDto) {
        log.info("User with id = {} trying to add new item request", userId);
        return requestClient.add(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("User with id = {} trying to fetch list of own item requests", userId);
        return requestClient.findByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllButNotUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "25", required = false) @Positive int size) {
        log.info("User with id = {} trying to fetch list of item requests", userId);
        return requestClient.findAllButNotUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long requestId) {
        log.info("User with id = {} trying to fetch item request with id = {} ", userId, requestId);
        return requestClient.get(userId, requestId);
    }
}
