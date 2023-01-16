package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.sevice.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BookingServiceImpl bookingService;

    private Long ownerId;
    private ItemDto itemDto;
    private Long itemId;
    private Long bookerId;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("userDtoName");
        owner.setEmail("owner@email");
        ownerId = userService.saveUser(owner).getId();

        itemDto = new ItemDto();
        itemDto.setName("itemDtoName");
        itemDto.setDescription("itemDtoDescription");
        itemDto.setAvailable(true);
        itemId = itemService.createItem(itemDto, ownerId).getId();

        UserDto booker = new UserDto();
        booker.setName("bookerName");
        booker.setEmail("booker@email");
        bookerId = userService.saveUser(booker).getId();

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.of(2000, 12, 31, 23, 59));
        bookingDto.setEnd(LocalDateTime.of(2001, 12, 31, 23, 59));
        bookingService.createBooking(bookingDto, bookerId);

        bookingDto.setStart(LocalDateTime.of(2030, 12, 31, 23, 59));
        bookingDto.setEnd(LocalDateTime.of(2031, 12, 31, 23, 59));
        bookingService.createBooking(bookingDto, bookerId);

        commentDto = new CommentDto();
        commentDto.setText("text");
        itemService.createComment(itemId, commentDto, bookerId);
    }

    @Test
    void getItemById() {
        ItemInfoDto resultItemInfoDto = itemService.getItemById(itemId, ownerId);

        assertThat(itemDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "requestId")
                .isEqualTo(resultItemInfoDto);
    }

    @Test
    void getItemById_whenItemNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemService.getItemById(99L, ownerId));
        assertEquals(exception.getMessage(), "Item не найдена. itemId = 99");
    }

    @Test
    void getAllItemsByOwnerId_whenItemHasBooking_thenReturnItemListWithBookings() {
        final List<ItemInfoDto> resultList = itemService.getAllItemsByOwnerId(ownerId, PageRequest.ofSize(10));

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        ItemInfoDto resultItemInfoDto = resultList.get(0);
        assertThat(itemDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "requestId")
                .isEqualTo(resultItemInfoDto);
    }

    @Test
    void getAllItemsByOwnerId_whenItemHasNoBooking_thenReturnItemListWithoutBookings() {
        ItemDto itemWithoutBookings = new ItemDto();
        itemWithoutBookings.setName("itemWithoutBookingsName");
        itemWithoutBookings.setDescription("itemWithoutBookingsDescription");
        itemWithoutBookings.setAvailable(true);
        itemService.createItem(itemWithoutBookings, ownerId);

        final List<ItemInfoDto> resultList = itemService.getAllItemsByOwnerId(ownerId, PageRequest.ofSize(10));

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        ItemInfoDto resultItemInfoDto = resultList.get(1);
        assertThat(itemWithoutBookings)
                .usingRecursiveComparison()
                .ignoringFields("id", "requestId")
                .isEqualTo(resultItemInfoDto);
    }

    @Test
    void createItem() {
        ItemDto resultItemDto = itemService.createItem(itemDto, ownerId);

        assertThat(resultItemDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(itemDto);
    }

    @Test
    void createItem_whenUserNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemService.createItem(itemDto, Long.MAX_VALUE));
        assertEquals(exception.getMessage(), "User не найден. userId = " + Long.MAX_VALUE);
    }

    @Test
    void createItem_whenRequestNotFound_thenThrowException() {
        itemDto.setRequestId(99L);
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemService.createItem(itemDto, ownerId));
        assertEquals(exception.getMessage(), "Запрос не найден");
    }

    @Test
    void updateItem() {
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setId(itemId);
        updateItemDto.setName("newItemDtoName");
        updateItemDto.setDescription("newItemDtoDescription");
        updateItemDto.setAvailable(false);
        ItemDto resultItemDto = itemService.updateItem(itemId, updateItemDto, ownerId);

        assertThat(resultItemDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(updateItemDto);
    }

    @Test
    void updateItem_whenUserHasNoItem_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemService.updateItem(itemId, itemDto, 99));
        assertEquals(exception.getMessage(), "Данная вещь не принадлежит пользователю. " +
                "Редактировать вещь может только ее владелец. " +
                "itemId = " + itemId);
    }

    @Test
    void findItems() {
        List<ItemDto> resultList = itemService.findItems("Description", PageRequest.ofSize(10));

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        ItemDto resultItemDto = resultList.get(0);
        assertThat(itemDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "requestId")
                .isEqualTo(resultItemDto);
    }

    @Test
    void findItems_whenTextIsNull_thenThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.findItems(null, PageRequest.ofSize(10)));
        assertEquals(exception.getMessage(), "Текстовый запрос для поиска не должен быть null.");
    }

    @Test
    void findItems_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> resultList = itemService.findItems("", PageRequest.ofSize(10));

        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    void createComment() {
        CommentDto resultComment = itemService.createComment(itemId, commentDto, bookerId);

        assertEquals(commentDto.getText(), resultComment.getText());
    }

    @Test
    void createComment_whenUserNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> itemService.createComment(itemId, commentDto, 99L));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void createComment_whenAuthorHasNoFinishedBookings_thenThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(itemId, commentDto, ownerId));
        assertEquals(exception.getMessage(), "У автора нет завершенных бронирований вещи.");
    }

}