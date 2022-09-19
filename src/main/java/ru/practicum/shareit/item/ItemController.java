package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable long itemId) {
        log.info("Получен запрос к эндпоинту: GET /items/{}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    List<ItemDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос к эндпоинту: GET /items");
        return itemService.getAllItemsByOwnerId(userId);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: POST /items, Создан объект из тела запроса:'{}'", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId,
                       @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    List<ItemDto> findItems(@RequestParam String text) {
        log.info("Получен запрос к эндпоинту: GET /items/search?text={}", text);
        return itemService.findItems(text);
    }

}
