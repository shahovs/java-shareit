package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requester);
        itemRequest.setCreated(created);
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> itemsOfRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        ItemRequestDto.RequestorDto requesterDto = new ItemRequestDto.RequestorDto();
        requesterDto.setId(itemRequest.getRequestor().getId());
        requesterDto.setName(itemRequest.getRequestor().getName());
        requesterDto.setEmail(itemRequest.getRequestor().getEmail());
        itemRequestDto.setRequestorDto(requesterDto);

        List<ItemRequestDto.ItemDto> itemDtos = new ArrayList<>();
        if (itemsOfRequest != null && !itemsOfRequest.isEmpty()) {
            long requestId = itemRequest.getId();
            for (Item item : itemsOfRequest) {
                ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
                itemDto.setId(item.getId());
                itemDto.setName(item.getName());
                itemDto.setDescription(item.getDescription());
                itemDto.setAvailable(item.getAvailable());
                itemDto.setRequestId(requestId);
                itemDtos.add(itemDto);
            }
        }
        itemRequestDto.setItems(itemDtos);

        return itemRequestDto;
    }

    public static List<ItemRequestDto> toItemRequestDtos(List<ItemRequest> itemRequests, List<Item> itemsOfRequests) {
        // если вещей к запросам нет, то сразу преобразуем запросы в dto и возвращаем их
        if (itemsOfRequests.isEmpty()) {
            return itemRequests.stream()
                    .map((itemRequest) -> ItemRequestMapper.toItemRequestDto(itemRequest, null))
                    .collect(Collectors.toList());
        }
        // иначе распределяем (группируем) вещи по запросам (создаем Map)
        Map<ItemRequest, List<Item>> itemsByRequests = itemsOfRequests.stream()
                .collect(Collectors.groupingBy(Item::getRequest));
        for (ItemRequest itemRequest : itemRequests) {
            itemsByRequests.putIfAbsent(itemRequest, null);
        }
        // готовую мапу преобразуем в список dtos
        List<ItemRequestDto> itemRequestDtos = itemsByRequests.entrySet().stream()
                .map((entry) -> ItemRequestMapper.toItemRequestDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
        return itemRequestDtos;
    }

}
