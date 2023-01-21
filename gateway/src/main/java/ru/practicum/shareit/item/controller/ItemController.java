package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Positive @PathVariable long itemId) {
        log.info("запрос к эндпоинту: GET /items/{}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                int fromElement,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("запрос к эндпоинту: GET /items from: {}, size: {}", fromElement, size);
        return itemClient.getAllItemsByOwnerId(userId, fromElement, size);
    }

    @PostMapping
    ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("запрос к эндпоинту: POST /items, Создан объект из тела запроса:'{}'", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Positive @PathVariable long itemId,
                                      @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    ResponseEntity<Object> findItems(@RequestParam String text,
                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int fromElement,
                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("запрос к эндпоинту: GET /items/search?text={} from: {}, size: {}", text, fromElement, size);
        return itemClient.findItems(text, fromElement, size);
    }

    @PostMapping("/{itemId}/comment")
    ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                                         @Positive @PathVariable long itemId,
                                         @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("запрос к эндпоинту: POST /items/{itemId}/comment, Создан объект из тела запроса:'{}'", commentDto);
        return itemClient.createComment(authorId, itemId, commentDto);
    }

}
