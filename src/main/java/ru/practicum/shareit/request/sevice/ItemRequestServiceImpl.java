package ru.practicum.shareit.request.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getItemRequestById(long requestId, long userId) {
        validateUserId(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ObjectDidntFoundException("Запрос на вещь не найден"));
        List<Item> itemsOfRequest = itemRepository.findAllByRequest(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsOfRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByRequester(long requesterId) {
        validateUserId(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(requesterId);
        List<Item> itemsOfRequests = itemRepository.findAllByRequest_Requestor_Id(requesterId);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.toItemRequestDtos(itemRequests, itemsOfRequests);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getItemRequestsOfOtherRequesters(long requesterId, int fromElement, int size) {
        Sort sortByCreatedDesc = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = MyPageRequest.of(fromElement, size, sortByCreatedDesc);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(pageable);
        List<ItemRequest> itemRequests = itemRequestPage.stream()
                .filter(itemRequest -> itemRequest.getRequestor().getId() != requesterId)
                .collect(Collectors.toList());
        List<Item> itemsOfRequests = itemRepository.findAllByRequestIn(itemRequests);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.toItemRequestDtos(itemRequests, itemsOfRequests);
        return itemRequestDtos;
    }

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new ObjectDidntFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest, null);
    }

    private void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectDidntFoundException("Пользователь не найден");
        }
    }

}
