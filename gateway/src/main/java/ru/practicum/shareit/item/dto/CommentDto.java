package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;


@Getter
@Setter
public class CommentDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String text;

    private String authorName;

    private LocalDate created;

}
