package ru.yandex.practicum.user.dto;

import lombok.Value;

@Value
public class UserUpdateRequest {
    String email;
    String name;

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }
}
