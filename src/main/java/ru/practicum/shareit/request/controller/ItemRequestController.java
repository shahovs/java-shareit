package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.sevice.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Positive @PathVariable long requestId) {
        log.info("Получен запрос к эндпоинту: GET /requests/{}", requestId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @GetMapping
    List<ItemRequestDto> getItemRequestsByRequester(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        log.info("Получен запрос к эндпоинту: GET /requests/");
        return itemRequestService.getItemRequestsByRequester(requesterId);
    }

    @GetMapping("/all") /*GET /requests/all?from={from}&size={size}*/
    List<ItemRequestDto> getItemRequestsOfOtherRequesters(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                                          @PositiveOrZero @RequestParam(name = "from",
                                                                  defaultValue = "0") int fromElement,
                                                          @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: GET /requests/all from: {}, size: {}", fromElement, size);
        return itemRequestService.getItemRequestsOfOtherRequesters(requesterId, fromElement, size);
    }

    @PostMapping
    ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                     @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту: POST /requests, Создан объект из тела запроса:'{}'", itemRequestDto);
        return itemRequestService.createItemRequest(itemRequestDto, requesterId);
    }

}
