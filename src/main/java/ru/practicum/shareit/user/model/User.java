package ru.practicum.shareit.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;

    private String name;
    private String email;
}
