package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStatus(booking.getStatus());

        Item item = booking.getItem();
        BookingDto.ItemDto itemDto = new BookingDto.ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwner().getId());
        bookingDto.setItem(itemDto);

        User booker = booking.getBooker();
        BookingDto.UserDto bookerDto = new BookingDto.UserDto();
        bookerDto.setId(booker.getId());
        bookerDto.setName(booker.getName());
        bookerDto.setEmail(booker.getEmail());
        bookingDto.setBooker(bookerDto);

        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static List<BookingDto> toBookingDtos(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
