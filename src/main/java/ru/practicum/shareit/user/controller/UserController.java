package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    UserDto getUserById(@Positive @PathVariable long userId) {
        log.info("Получен запрос к эндпоинту: GET /users/{}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    List<UserDto> getAllUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int fromElement,
                              @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: GET /users/ from: {}, size: {}", fromElement, size);
        final PageRequest pageRequest = MyPageRequest.of(fromElement, size);
        return userService.getAllUsers(pageRequest);
    }

    @PostMapping
    UserDto saveUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: POST /users, Создан объект из тела запроса:'{}'", userDto);
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@Positive @PathVariable long userId,
                       @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: PATCH /users, Создан объект из тела запроса:'{}'", userDto);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@Positive @PathVariable long userId) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

}
