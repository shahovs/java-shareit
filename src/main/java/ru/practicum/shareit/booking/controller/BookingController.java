package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.sevice.BookingService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Min(1L) @PathVariable long bookingId) {
        log.info("Получен запрос к эндпоинту: GET /bookings/{}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    List<BookingDto> getAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                             @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Получен запрос к эндпоинту: GET /bookings. X-Sharer-User-Id = {}", bookerId);
        return bookingService.getAllBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    List<BookingDto> getAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Получен запрос к эндпоинту: GET /bookings/owner. X-Sharer-User-Id = {}", ownerId);
        return bookingService.getAllBookingsByOwnerId(ownerId, state);
    }

    @PostMapping
    BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос к эндпоинту: POST /bookings, Создан объект из тела запроса:'{}'", bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDto changeStatusOfBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                     @Min(1L) @PathVariable long bookingId,
                                     @RequestParam() Boolean approved) {
        return bookingService.changeStatusOfBooking(ownerId, bookingId, approved);
    }

}
