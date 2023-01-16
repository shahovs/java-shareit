package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.sevice.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("userDtoName");
        userDto.setEmail("userDto@email");
    }

    @SneakyThrows
    @Test
    void getUserById() {
        when(userService.getUserById(1))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1")) // или: "/users/{userId}", "1"
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .getUserById(1L);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1))
                .getAllUsers(PageRequest.ofSize(10));
    }

    @SneakyThrows
    @Test
    void getAllUsers_thenReturnSingletonList() {
        when(userService.getAllUsers(PageRequest.ofSize(10)))
                .thenReturn(Collections.singletonList(userDto));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"name\":\"userDtoName\",\"email\":\"userDto@email\"}]"));

        verify(userService, times(1))
                .getAllUsers(PageRequest.ofSize(10));
    }

    @SneakyThrows
    @Test
    void saveUser() {
        when(userService.saveUser(any()))
                .thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService, times(1))
                .saveUser(any());
    }

    @SneakyThrows
    @Test
    void saveUser_whenNameIsNotValid_thenReturnedBadRequest() {
        userDto.setName("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any());
    }

    @SneakyThrows
    @Test
    void updateUser() {
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService, times(1))
                .updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIdIsNotValid_thenReturnedError() {
        mockMvc.perform(patch("/users/{userId}", -1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(userService, never()).updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/1")) // или: "/users/{userId}", "1"
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUser(1L);
    }

}