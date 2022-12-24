package ru.practicum.shareit.booking.sevice;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state);

    List<BookingDto> getAllBookingsByOwnerId(long ownerId, BookingState state);

    BookingDto createBooking(BookingDto bookingDto, long userId);

    BookingDto changeStatusOfBooking(long ownerId, long bookingId, Boolean approved);

}
