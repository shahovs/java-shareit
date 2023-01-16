package ru.practicum.shareit.booking.sevice;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state, int fromElement, int size);

    List<BookingDto> getAllBookingsByOwnerId(long ownerId, BookingState state, int fromElement, int size);

    BookingDto createBooking(BookingDto bookingDto, long userId);

    BookingDto changeStatusOfBooking(long ownerId, long bookingId, Boolean approved);

}
