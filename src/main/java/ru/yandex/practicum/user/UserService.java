package ru.yandex.practicum.user;

import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserUpdateRequest;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto saveUser(User user);

    UserDto getUserById(Long userId);

    UserDto updateUserById(Long userId, UserUpdateRequest user);

    void deleteUserById(Long userId);
}