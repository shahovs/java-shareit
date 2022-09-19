package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItemsByOwnerId(long userId);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId);

    List<ItemDto> findItems(String text);

}
