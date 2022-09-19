package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    public User getUserById(long userId) {
        return users.get(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User saveUser(User newUser) {
        checkUniqueEmail(newUser.getEmail());
        long userId = generateId();
        newUser.setId(userId);
        users.put(userId, newUser);
        return newUser;
    }

    public void updateUserName(long userId, String name) {
        users.get(userId).setName(name);
    }

    public void updateUserEmail(long userId, String email) {
        checkUniqueEmail(email);
        users.get(userId).setEmail(email);
    }

    public void deleteUser(long userId) {
        users.remove(userId);
    }

    private void checkUniqueEmail(String newEmail) {
        if (users.values().stream()
                .anyMatch((user) -> user.getEmail().equals(newEmail))) {
            throw new ConflictException("Пользователь с таким email уже существует. Email: " + newEmail);
        }
    }

    private long generateId() {
        return ++id;
    }

}
