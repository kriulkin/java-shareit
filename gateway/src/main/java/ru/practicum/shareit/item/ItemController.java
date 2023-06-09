package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        return itemClient.get(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Validated(ItemDto.New.class) @RequestBody ItemDto itemDto) {
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated(ItemDto.UpdateFields.class) @RequestBody ItemDto itemDto,
                                         @PathVariable long itemId) {
        log.info("User with id = {} trying to update item with id = {}", itemId, userId);
        itemDto.setId(itemId);
        return itemClient.update(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "25", required = false) @Positive int size) {
        log.info("User with id = {} trying to fetch item list", userId);
        return itemClient.findByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "text") String term,
                                         @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "25", required = false) @Positive int size) {
        log.info("User trying to search items by term \"{}\"", term);
        return itemClient.search(userId, term, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommant(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated @RequestBody CommentDto commentDto,
                                             @PathVariable long itemId) {
        log.info("User trying to post comment to item with с id = {}", itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
