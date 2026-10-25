package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRequest {

    @NotBlank(message = "Description can't be empty")
    private String description;
}
