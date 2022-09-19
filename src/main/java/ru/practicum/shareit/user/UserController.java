package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable long userId) {
        log.info("Получен запрос к эндпоинту: GET /users/{}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    List<UserDto> getAllUsers() {
        log.info("Получен запрос к эндпоинту: GET /users/");
        return userService.getAllUsers();
    }

    @PostMapping
    UserDto saveUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: POST /users, Создан объект из тела запроса:'{}'", userDto);
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@PathVariable long userId,
                       @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: PATCH /users, Создан объект из тела запроса:'{}'", userDto);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

}
