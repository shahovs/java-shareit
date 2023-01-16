package ru.practicum.shareit.request.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ItemServiceImpl itemService;

    private ItemRequestDto itemRequestDto;
    private Long requesterId;
    private Long ownerId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        UserDto requester = new UserDto();
        requester.setName("requesterName");
        requester.setEmail("requester@email");
        requesterId = userService.saveUser(requester).getId();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequestDescription");

        ItemRequestDto.RequestorDto requesterDto = new ItemRequestDto.RequestorDto();
        requesterDto.setId(requesterId);
        requesterDto.setName(requester.getName());
        requesterDto.setEmail(requester.getEmail());
        itemRequestDto.setRequestorDto(requesterDto);

        requestId = itemRequestService.createItemRequest(itemRequestDto, requesterId).getId();

        UserDto owner = new UserDto();
        owner.setName("ownerName");
        owner.setEmail("owner@email");
        ownerId = userService.saveUser(owner).getId();

        ItemDto item = new ItemDto();
        item.setName("itemDtoName");
        item.setDescription("itemDtoDescription");
        item.setAvailable(true);
        item.setRequestId(requestId);
        Long itemId = itemService.createItem(item, ownerId).getId();

        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        itemDto.setId(itemId);
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(requestId);
        itemRequestDto.setItems(List.of(itemDto));
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto itemRequestById = itemRequestService.getItemRequestById(requestId, requesterId);

        assertThat(itemRequestById)
                .usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(itemRequestDto);
    }

    @Test
    void getItemRequestById_whenRequestNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemRequestService.getItemRequestById(99L, requesterId));
        assertEquals(exception.getMessage(), "Запрос на вещь не найден");
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, 99L));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void getItemRequestsByRequester() {
        List<ItemRequestDto> resultList = itemRequestService.getItemRequestsByRequester(requesterId);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        ItemRequestDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(itemRequestDto);
    }

    @Test
    void getItemRequestsByRequester_whenNoItems() {
        UserDto requester = new UserDto();
        requester.setName("newName");
        requester.setEmail("new@email");
        requesterId = userService.saveUser(requester).getId();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequestDescription");

        ItemRequestDto.RequestorDto requesterDto = new ItemRequestDto.RequestorDto();
        requesterDto.setId(requesterId);
        requesterDto.setName(requester.getName());
        requesterDto.setEmail(requester.getEmail());
        itemRequestDto.setRequestorDto(requesterDto);

        itemRequestDto.setItems(new ArrayList<>());

        itemRequestService.createItemRequest(itemRequestDto, requesterId);

        List<ItemRequestDto> resultList = itemRequestService.getItemRequestsByRequester(requesterId);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        ItemRequestDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(itemRequestDto);
    }

    @Test
    void getItemRequestsOfOtherRequesters() {
        List<ItemRequestDto> resultList = itemRequestService.getItemRequestsOfOtherRequesters(ownerId, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        ItemRequestDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(itemRequestDto);
    }

    @Test
    void createItemRequest() {
        UserDto userDto = new UserDto();
        userDto.setName("userDtoName");
        userDto.setEmail("userDto@email");
        Long userDtoId = userService.saveUser(userDto).getId();

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        ItemRequestDto.RequestorDto requesterDto = new ItemRequestDto.RequestorDto();
        requesterDto.setId(userDtoId);
        requesterDto.setName(userDto.getName());
        requesterDto.setEmail(userDto.getEmail());
        itemRequestDto.setRequestorDto(requesterDto);
        itemRequestDto.setItems(new ArrayList<>());

        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestDto, userDtoId);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(itemRequestDto);
    }

    @Test
    void createItemRequest_whenRequesterNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestDto, 99L));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

}