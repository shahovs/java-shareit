package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    ItemInfoDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable long itemId) {
        log.info("Получен запрос к эндпоинту: GET /items/{}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    List<ItemInfoDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(name = "from", defaultValue = "0")
                                           int fromElement,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: GET /items from: {}, size: {}", fromElement, size);
        final PageRequest pageRequest = MyPageRequest.of(fromElement, size);
        return itemService.getAllItemsByOwnerId(userId, pageRequest);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: POST /items, Создан объект из тела запроса:'{}'", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable long itemId,
                       @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    List<ItemDto> findItems(@RequestParam String text,
                            @RequestParam(name = "from", defaultValue = "0") int fromElement,
                            @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: GET /items/search?text={} from: {}, size: {}", text, fromElement, size);
        final PageRequest pageRequest = MyPageRequest.of(fromElement, size);
        return itemService.findItems(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long authorId,
                             @PathVariable long itemId,
                             @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту: POST /items/{itemId}/comment, Создан объект из тела запроса:'{}'",
                commentDto);
        return itemService.createComment(itemId, commentDto, authorId);
    }

}
