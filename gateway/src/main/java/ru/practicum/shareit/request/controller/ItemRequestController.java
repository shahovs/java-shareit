package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Positive @PathVariable long requestId) {
        log.info("запрос к эндпоинту: GET /requests/{}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping
    ResponseEntity<Object> getItemRequestsByRequester(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        log.info("запрос к эндпоинту: GET /requests/");
        return itemRequestClient.getItemRequestsByRequester(requesterId);
    }

    @GetMapping("/all") /*GET /requests/all?from={from}&size={size}*/
    ResponseEntity<Object> getItemRequestsOfOtherRequesters(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                                            @PositiveOrZero @RequestParam(name = "from",
                                                                    defaultValue = "0") int fromElement,
                                                            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("запрос к эндпоинту: GET /requests/all from: {}, size: {}", fromElement, size);
        return itemRequestClient.getItemRequestsOfOtherRequesters(requesterId, fromElement, size);
    }

    @PostMapping
    ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                             @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("запрос к эндпоинту: POST /requests, Создан объект из тела запроса:'{}'", itemRequestDto);
        return itemRequestClient.createItemRequest(requesterId, itemRequestDto);
    }

}
