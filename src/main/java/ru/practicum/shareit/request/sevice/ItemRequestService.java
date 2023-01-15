package ru.practicum.shareit.request.sevice;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getItemRequestById(long requestId, long userId);

    List<ItemRequestDto> getItemRequestsByRequester(long requesterId);

    List<ItemRequestDto> getItemRequestsOfOtherRequesters(long requesterId, int fromElement, int size);

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long requesterId);

}
