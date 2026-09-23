package ru.yandex.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "email must be in email format")
    @NotBlank(message = "email can not be empty")
    private String email;
    @NotBlank(message = "name can not be empty")
    private String name;
}