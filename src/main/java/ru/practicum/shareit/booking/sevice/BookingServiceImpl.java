package ru.practicum.shareit.booking.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.exception.OtherException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;
    public final UserRepository userRepository;
    public final ItemRepository itemRepository;

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectDidntFoundException("Заказ с таким id не найден"));
        if (Objects.equals(userId, booking.getBooker().getId())
                || Objects.equals(userId, booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new ObjectDidntFoundException("Данный пользователь не является ни создателем заказа, ни владельцем вещи");
    }

    @Override
    public List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state, int fromElement, int size) {
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new ObjectDidntFoundException("Пользователь не найден"));

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = MyPageRequest.of(fromElement, size, sortByStartDesc);

        final List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBooker(booker, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerAndCurrentOrderByStartDesc(booker, LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(booker, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(booker, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Неверный статус");
        }
        return BookingMapper.toBookingDtos(result);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(long ownerId, BookingState state, int fromElement, int size) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new ObjectDidntFoundException("Пользователь не найден"));

        int fromPage = fromElement / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        final List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItem_OwnerOrderByStartDesc(owner, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItem_OwnerAndCurrentOrderByStartDesc(owner.getId(),
                        LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findAllByItem_OwnerAndEndBeforeOrderByStartDesc(owner, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findAllByItem_OwnerAndStartAfterOrderByStartDesc(owner, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Неверный статус");
        }
        return BookingMapper.toBookingDtos(result);
    }

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, long userId) {
        final Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ObjectDidntFoundException("Item не найден"));
        final User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectDidntFoundException("Пользователь не найден"));
        validateBooking(bookingDto, userId, item);

        bookingDto.setStatus(BookingStatus.WAITING);
        final Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, item));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto changeStatusOfBooking(long ownerId, long bookingId, Boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectDidntFoundException("Заказ с таким id не найден"));
        if (Objects.equals(booking.getStatus(), BookingStatus.APPROVED)) {
            throw new ObjectAlreadyExistsException("Заказ уже имеет статус APPROVED. Изменение статуса невозможно");
        }
        if (Objects.equals(ownerId, booking.getItem().getOwner().getId())) {
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            final Booking savedBooking = bookingRepository.save(booking);
            return BookingMapper.toBookingDto(savedBooking);
        } else {
            throw new ObjectDidntFoundException("Ошибка. Статус заказа может менять только владелец вещи");
        }
    }

    private void validateBooking(BookingDto bookingDto, long userId, Item item) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Начало бронирования должно предшестовать окончанию бронирования");
        }
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Вещь не доступна для бронирования");
        }
        if (Objects.equals(userId, item.getOwner().getId())) {
            throw new OtherException("Пользователь не может заказать свою собственную вещь");
        }
    }

}
