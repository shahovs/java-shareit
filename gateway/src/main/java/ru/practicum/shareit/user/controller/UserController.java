package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    ResponseEntity<Object> getUserById(@Positive @PathVariable long userId) {
        log.info("запрос к эндпоинту: GET /users/{}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    ResponseEntity<Object> getAllUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int fromElement,
                              @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("запрос к эндпоинту: GET /users/ from: {}, size: {}", fromElement, size);
        return userClient.getAllUsers(fromElement, size);
    }

    @PostMapping
    ResponseEntity<Object> saveUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("запрос к эндпоинту: POST /users, Создан объект из тела запроса:'{}'", userDto);
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{userId}")
    ResponseEntity<Object> updateUser(@Positive @PathVariable long userId,
                       @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.info("запрос к эндпоинту: PATCH /users, Создан объект из тела запроса:'{}'", userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    void deleteUser(@Positive @PathVariable long userId) {
        log.info("запрос к эндпоинту: DELETE /users/{}", userId);
        userClient.deleteUser(userId);
    }

}
