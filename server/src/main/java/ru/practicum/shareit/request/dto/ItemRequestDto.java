package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {

    private Long id;
    private String description;
    private RequestorDto requestorDto;
    private LocalDateTime created;
    private List<ItemDto> items;

    @Getter
    @Setter
    public static class RequestorDto {
        private Long id;
        private String name;
        private String email;
    }

    @Getter
    @Setter
    public static class ItemDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }

}
