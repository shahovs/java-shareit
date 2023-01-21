package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemInfoDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentInfoDto> comments;

    @Getter
    @Setter
    public static class BookingInfoDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
    }

    @Getter
    @Setter
    public static class CommentInfoDto {
        private Long id;
        private String text;
        private Long itemId;
        private String authorName;
        private LocalDate created;
    }

}
