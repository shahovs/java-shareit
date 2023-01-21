package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;

    @Getter
    @Setter
    public static class ItemDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long ownerId;
    }

    @Getter
    @Setter
    public static class UserDto {
        private Long id;
        private String name;
        private String email;
    }

}
