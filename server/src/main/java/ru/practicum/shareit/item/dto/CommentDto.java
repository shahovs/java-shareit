package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class CommentDto {

    private Long id;

    private String text;

//    private ItemDto item;

    private String authorName;

    private LocalDate created;

//    @Getter
//    @Setter
//    public static class ItemDto {
//        private Long id;
//        private String name;
//        private String description;
//        private Boolean available;
//        private Long requestId;
//    }

}
