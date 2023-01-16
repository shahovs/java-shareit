package ru.practicum.shareit.booking.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.exception.OtherException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    BookingServiceImpl bookingService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ItemServiceImpl itemService;

    private BookingDto bookingDto;
    private Long bookingId;
    private Long ownerId;
    private Long bookerId;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("userDtoName");
        owner.setEmail("userDto@email");
        ownerId = userService.saveUser(owner).getId();

        UserDto booker = new UserDto();
        booker.setName("bookerName");
        booker.setEmail("booker@email");
        bookerId = userService.saveUser(booker).getId();

        ItemDto item = new ItemDto();
        item.setName("itemDtoName");
        item.setDescription("itemDtoDescription");
        item.setAvailable(true);
        Long itemId = itemService.createItem(item, ownerId).getId();

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2030, 12, 31, 23, 59, 59));
        bookingDto.setEnd(LocalDateTime.of(2031, 12, 31, 23, 59, 59));
        bookingDto.setItemId(itemId);
        bookingDto.setStatus(BookingStatus.WAITING);

        BookingDto.ItemDto itemDto = new BookingDto.ItemDto();
        itemDto.setId(itemId);
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(ownerId);
        bookingDto.setItem(itemDto);

        BookingDto.UserDto bookerDto = new BookingDto.UserDto();
        bookerDto.setId(bookerId);
        bookerDto.setName(booker.getName());
        bookerDto.setEmail(booker.getEmail());
        bookingDto.setBooker(bookerDto);

        bookingId = bookingService.createBooking(bookingDto, bookerId).getId();
    }

    @Test
    void getBookingById_whenBookerFindBooking_thenReturnItem() {
        BookingDto bookingById = bookingService.getBookingById(bookingId, bookerId);

        assertThat(bookingById)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getBookingById_whenOwnerFindBookingOfHisItem_thenReturnItem() {
        BookingDto bookingById = bookingService.getBookingById(bookingId, ownerId);

        assertThat(bookingById)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getBookingById_whenUserIsNotBookerOrOwner_thenReturnException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.getBookingById(bookingId, 99L));
        assertEquals(exception.getMessage(),
                "Данный пользователь не является ни создателем заказа, ни владельцем вещи");
    }

    @Test
    void getBookingById_whenBookingNotFound_thenReturnException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.getBookingById(99L, ownerId));
        assertEquals(exception.getMessage(), "Заказ с таким id не найден");
    }

    @Test
    void getAllBookingsByBookerId() {
        List<BookingDto> resultList = bookingService.getAllBookingsByBookerId(bookerId, BookingState.ALL, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByBookerId_whenBookerNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.getAllBookingsByBookerId(99L, BookingState.ALL, 0, 10));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void getAllBookingsByBookerId_withStatusFuture_thenReturnBookings() {
        List<BookingDto> resultList = bookingService.getAllBookingsByBookerId(bookerId, BookingState.FUTURE, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByBookerId_withStatusCurrent_thenReturnBookings() {
        bookingDto.setStart(LocalDateTime.now());
        bookingService.createBooking(bookingDto, bookerId);
        List<BookingDto> resultList = bookingService.getAllBookingsByBookerId(bookerId, BookingState.CURRENT, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByBookerId_withStatusWaiting_thenReturnBookings() {
        List<BookingDto> resultList = bookingService.getAllBookingsByBookerId(bookerId, BookingState.WAITING, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByBookerId_withStatusRejected_thenReturnBookings() {
        bookingService.changeStatusOfBooking(ownerId, bookingId, false);
        bookingDto.setStatus(BookingStatus.REJECTED);
        List<BookingDto> resultList = bookingService.getAllBookingsByBookerId(bookerId, BookingState.REJECTED, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByBookerId_whenIllegalState_thenThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByBookerId(bookerId, BookingState.ILLEGAL_STATE, 0, 10));
        assertEquals(exception.getMessage(), "Неверный статус");
    }

    @Test
    void getAllBookingsByOwnerId() {
        List<BookingDto> resultList = bookingService.getAllBookingsByOwnerId(ownerId, BookingState.ALL, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByOwnerId_whenOwnerNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.getAllBookingsByOwnerId(99L, BookingState.ALL, 0, 10));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void getAllBookingsByOwnerId_withStatusFuture_thenReturnBookings() {
        List<BookingDto> resultList = bookingService.getAllBookingsByOwnerId(ownerId, BookingState.FUTURE, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByOwnerId_withStatusCurrent_thenReturnBookings() {
        bookingDto.setStart(LocalDateTime.now());
        bookingService.createBooking(bookingDto, bookerId);
        List<BookingDto> resultList = bookingService.getAllBookingsByOwnerId(ownerId, BookingState.CURRENT, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByOwnerId_withStatusWaiting_thenReturnBookings() {
        List<BookingDto> resultList = bookingService.getAllBookingsByOwnerId(ownerId, BookingState.WAITING, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByOwnerId_withStatusRejected_thenReturnBookings() {
        bookingService.changeStatusOfBooking(ownerId, bookingId, false);
        bookingDto.setStatus(BookingStatus.REJECTED);
        List<BookingDto> resultList = bookingService.getAllBookingsByOwnerId(ownerId, BookingState.REJECTED, 0, 10);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        BookingDto result = resultList.get(0);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void getAllBookingsByOwnerId_whenIllegalState_thenThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByOwnerId(ownerId, BookingState.ILLEGAL_STATE, 0, 10));
        assertEquals(exception.getMessage(), "Неверный статус");
    }

    @Test
    void createBooking() {
        BookingDto result = bookingService.createBooking(bookingDto, bookerId);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowException() {
        bookingDto.setItemId(99L);
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.createBooking(bookingDto, bookerId));
        assertEquals(exception.getMessage(), "Item не найден");
    }

    @Test
    void createBooking_whenBookerNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.createBooking(bookingDto, 99L));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenThrowException() {
        ItemDto item = new ItemDto();
        item.setName("itemDtoName");
        item.setDescription("itemDtoDescription");
        item.setAvailable(false);
        Long itemId = itemService.createItem(item, ownerId).getId();
        bookingDto.setItemId(itemId);

        ItemIsNotAvailableException exception = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.createBooking(bookingDto, bookerId));
        assertEquals(exception.getMessage(), "Вещь не доступна для бронирования");
    }

    @Test
    void createBooking_whenStartIsNotBeforeEnd_thenThrowException() {
        bookingDto.setStart(bookingDto.getEnd().plusSeconds(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingDto, bookerId));
        assertEquals(exception.getMessage(), "Начало бронирования должно предшестовать окончанию бронирования");
    }

    @Test
    void createBooking_whenOwnerTryBookHisOwnItem_thenThrowException() {
        OtherException exception = assertThrows(OtherException.class,
                () -> bookingService.createBooking(bookingDto, ownerId));
        assertEquals(exception.getMessage(), "Пользователь не может заказать свою собственную вещь");
    }

    @Test
    void changeStatusOfBooking() {
        BookingDto result = bookingService.changeStatusOfBooking(ownerId, bookingId, true);
        bookingDto.setStatus(BookingStatus.APPROVED);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingDto);
    }

    @Test
    void changeStatusOfBooking_whenBookingNotFound_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.changeStatusOfBooking(ownerId, 99L, true));
        assertEquals(exception.getMessage(), "Заказ с таким id не найден");
    }

    @Test
    void changeStatusOfBooking_whenBookingAlreadyHasStatusApproved_thenThrowException() {
        bookingService.changeStatusOfBooking(ownerId, bookingId, true);

        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class,
                () -> bookingService.changeStatusOfBooking(ownerId, bookingId, true));
        assertEquals(exception.getMessage(), "Заказ уже имеет статус APPROVED. Изменение статуса невозможно");
    }

    @Test
    void changeStatusOfBooking_whenUserIsNotOwner_thenThrowException() {
        ObjectDidntFoundException exception = assertThrows(ObjectDidntFoundException.class,
                () -> bookingService.changeStatusOfBooking(bookerId, bookingId, true));
        assertEquals(exception.getMessage(), "Ошибка. Статус заказа может менять только владелец вещи");
    }

}