package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    private String email;
    private String name;
}