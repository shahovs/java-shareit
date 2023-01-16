package ru.practicum.shareit.user.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ObjectDidntFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setName("user1Name");
        user.setEmail("user1@email");
    }

    @Test
    void getUserById() {
        final Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findById(1L))
                .thenReturn(optionalUser);

        UserDto userDto = userService.getUserById(1L);

        assertNotNull(userDto);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_whenUserNotFound_thenObjectNotFoundExceptionThrow() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ObjectDidntFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers() {
        final PageImpl<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(PageRequest.ofSize(10)))
                .thenReturn(userPage);

        final List<UserDto> userDtos = userService.getAllUsers(PageRequest.ofSize(10));

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
        UserDto userDto = userDtos.get(0);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void saveUser() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userDto = userService.saveUser(UserMapper.toUserDto(new User()));

        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("newName");
        newUser.setEmail("new@email");
        UserDto newUserDto = UserMapper.toUserDto(newUser);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(newUser);
        userService.updateUser(1L, newUserDto);

        assertEquals(newUser.getName(), user.getName());
        assertEquals(newUser.getEmail(), user.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser_whenUserNotFound() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("newName");
        newUser.setEmail("new@email");
        UserDto newUserDto = UserMapper.toUserDto(newUser);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(ObjectDidntFoundException.class,
                () -> userService.updateUser(1L, newUserDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

}