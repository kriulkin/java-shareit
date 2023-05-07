package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    public final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable long itemId) {
        return itemService.get(itemId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated(ItemDto.New.class) @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated(ItemDto.UpdateFields.class) @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        log.info("Попытка обновить вещь с id = {} пользователем с id = {}", itemId, userId);
        itemDto.setId(itemId);
        return itemService.update(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Попытка получить список вещей пользователем с id = {}", userId);
        return itemService.findByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name="text") String term) {
        log.info("Попытка поиска вещей пользователем по строке \"{}\"", term);
        return itemService.search(term);
    }
}


