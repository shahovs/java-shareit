package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String email;

}
