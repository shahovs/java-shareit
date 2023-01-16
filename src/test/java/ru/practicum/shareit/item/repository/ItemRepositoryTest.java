package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    ItemRequest itemRequest;
    Item item1;
    Item item2;

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("user1Name");
        user1.setEmail("user1@email");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2Name");
        user2.setEmail("user2@email");
        user2 = userRepository.save(user2);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("requestDescription");
        itemRequest.setCreated(LocalDateTime.of(2000, 12, 31, 23, 59));
        itemRequest.setRequestor(user1);
        itemRequest = itemRequestRepository.save(itemRequest);

        item1 = new Item();
        item1.setName("item1Name");
        item1.setDescription("item1Description");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequest(itemRequest);
        itemRepository.save(item1);

        item2 = new Item();
        item2.setName("item2Name");
        item2.setDescription("item2Description");
        item2.setAvailable(false);
        item2.setOwner(user2);
        itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId() {
        List<Item> allItemsByOwnerId = itemRepository.findAllByOwnerId(user1.getId(), Pageable.unpaged());

        assertNotNull(allItemsByOwnerId);
        assertEquals(1, allItemsByOwnerId.size());
        Item result = allItemsByOwnerId.get(0);
        assertEquals(item1, result);
    }

    @Test
    void findAllByRequest() {
        List<Item> allItemsByRequest = itemRepository.findAllByRequest(itemRequest);

        assertNotNull(allItemsByRequest);
        assertEquals(1, allItemsByRequest.size());
        Item result = allItemsByRequest.get(0);
        assertEquals(item1, result);
    }

    @Test
    void findAllByRequestIn() {
        List<Item> allItemsByRequestIn = itemRepository.findAllByRequestIn(Collections.singletonList(itemRequest));

        assertNotNull(allItemsByRequestIn);
        assertEquals(1, allItemsByRequestIn.size());
        Item result = allItemsByRequestIn.get(0);
        assertEquals(item1, result);
    }

    @Test
    void findAllByRequest_Requestor_Id() {
        List<Item> allItemsByRequestorId = itemRepository.findAllByRequest_Requestor_Id(user1.getId());

        assertNotNull(allItemsByRequestorId);
        assertEquals(1, allItemsByRequestorId.size());
        Item result = allItemsByRequestorId.get(0);
        assertEquals(item1, result);
    }

    @Test
    void searchByName() {
        List<Item> result = itemRepository.search("teM1nAm", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    void searchByDescription() {
        List<Item> result = itemRepository.search("tem1dEScriptio", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    void searchNotAvailiable() {
        List<Item> result = itemRepository.search("item2Name", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

}