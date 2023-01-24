package ru.practicum.shareit.user.sevice;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers(PageRequest pageRequest);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);

}
