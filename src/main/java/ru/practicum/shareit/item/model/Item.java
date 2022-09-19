package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Item {
    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
