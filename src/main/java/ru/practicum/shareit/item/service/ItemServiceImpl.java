package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long userId) {
        List<Item> items = itemRepository.getAllItemsByOwnerId(userId);
        return mapItemsToItemDtos(items);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User owner = getOwner(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        item = itemRepository.saveItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long userId) {
        User owner = getOwner(userId);
        Item item = getItem(itemId);
        checkOwnerHasUpdatedItem(owner, item);
        changeValuesOfItem(itemDto, item);
        itemRepository.updateItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Текстовый запрос для поиска не должен быть null.");
        }
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        List<Item> foundItems = itemRepository.findItem(text);
        return mapItemsToItemDtos(foundItems);
    }

    private List<ItemDto> mapItemsToItemDtos(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    private Item getItem(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new ObjectDidntFoundException("Item не найдена. itemId = " + itemId);
        }
        return item;
    }

    private User getOwner(long userId) {
        User owner = userRepository.getUserById(userId);
        if (owner == null) {
            throw new ObjectDidntFoundException("User не найден. userId = " + userId);
        }
        return owner;
    }

    private void checkOwnerHasUpdatedItem(User owner, Item item) {
        if (!owner.equals(item.getOwner())) {
            throw new ObjectDidntFoundException("Данная вещь не принадлежит пользователю. " +
                    "Редактировать вещь может только ее владельец. " +
                    "item = " + item + " owner = " + owner);
        }
    }

    private void changeValuesOfItem(ItemDto itemDto, Item item) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
    }

}
