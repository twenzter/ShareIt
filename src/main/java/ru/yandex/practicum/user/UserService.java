package ru.yandex.practicum.user;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserUpdateRequest;

import java.util.Collection;

@Transactional(readOnly = true)
public interface UserService {
    Collection<UserDto> getAllUsers();

    @Transactional
    UserDto saveUser(User user);

    UserDto getUserById(Long userId);

    @Transactional
    UserDto updateUserById(Long userId, UserUpdateRequest user);

    @Transactional
    void deleteUserById(Long userId);
}