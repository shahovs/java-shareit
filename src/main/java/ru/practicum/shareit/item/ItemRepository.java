package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    public List<Item> getAllItemsByOwnerId(long userId) {
        return items.values().stream()
                .filter((item) -> item.getOwner().getId() == userId)
                .collect(toList());
    }

    public Item saveItem(Item item) {
        long itemId = generateId();
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }

    public List<Item> findItem(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter((item) ->
                        item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text))
                .collect(toList());
    }

    private long generateId() {
        return ++id;
    }

}
