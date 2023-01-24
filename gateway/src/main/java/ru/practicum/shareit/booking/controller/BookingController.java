package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.BookingState;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Positive @PathVariable long bookingId) {
        log.info("запрос к эндпоинту: GET /bookings/{}", bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    ResponseEntity<Object> getAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   int fromElement,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("запрос к эндпоинту: GET /bookings. X-Sharer-User-Id = {}, from: {}, size: {}", bookerId, fromElement,
                size);
        return bookingClient.getAllBookingByBookerId(bookerId, state, fromElement, size);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> getAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                  int fromElement,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("запрос к эндпоинту: GET /bookings/owner. X-Sharer-User-Id = {}, from: {}, size: {}", ownerId,
                fromElement, size);
        return bookingClient.getAllBookingsByOwnerId(ownerId, state, fromElement, size);
    }

    @PostMapping
    ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Create.class}) @RequestBody BookItemRequestDto bookItemRequestDto) {
        log.info("запрос к эндпоинту: POST /bookings, Создан объект из тела запроса:'{}'", bookItemRequestDto);
        if (bookItemRequestDto.getStart().isAfter(bookItemRequestDto.getEnd())) {
            throw new IllegalArgumentException("Начало бронирования должно предшестовать окончанию бронирования");
        }
        return bookingClient.createBooking(userId, bookItemRequestDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> changeStatusOfBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @Positive @PathVariable long bookingId,
                                                 @RequestParam() Boolean approved) {
        log.info("запрос к эндпоинту: PATCH /bookings, X-Sharer-User-Id = {}, approved = {}", ownerId, approved);
        return bookingClient.changeStatusOfBooking(ownerId, bookingId, approved);
    }

}
