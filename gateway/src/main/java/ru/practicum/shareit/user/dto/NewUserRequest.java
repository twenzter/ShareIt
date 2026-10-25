package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "email can not be empty")
    @Email(message = "email must be in email format")
    private String email;

    @NotBlank(message = "name can not be empty")
    private String name;
}