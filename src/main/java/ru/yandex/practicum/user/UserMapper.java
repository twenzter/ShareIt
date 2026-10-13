package ru.yandex.practicum.user;

import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserUpdateRequest;

public class UserMapper {
    public static User updateUser(User oldUser, UserUpdateRequest user) {
        return new User(oldUser.getId(),
                user.hasEmail() ? user.getEmail() : oldUser.getEmail(),
                user.hasName() ? user.getName() : oldUser.getName());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(),
                user.getEmail(),
                user.getName());
    }
}
