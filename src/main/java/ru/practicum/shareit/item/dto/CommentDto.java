package ru.practicum.shareit.item.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@EqualsAndHashCode

public class CommentDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String text;

    private Item item;

    private String authorName;

    private LocalDate created;

}
