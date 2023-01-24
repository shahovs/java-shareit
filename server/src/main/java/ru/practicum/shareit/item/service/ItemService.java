package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

@Service
public interface ItemService {

    ItemInfoDto getItemById(long itemId, long userId);

    List<ItemInfoDto> getAllItemsByOwnerId(long userId, PageRequest pageRequest);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, ItemDto itemDto, long userId);

    List<ItemDto> findItems(String text, PageRequest pageRequest);

    CommentDto createComment(long itemId, CommentDto commentDto, long authorId);

}
