package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private Long id;

    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    @Future(groups = {Create.class})
    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private String status;

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
