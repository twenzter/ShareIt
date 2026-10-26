package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentRequest {

    @NotBlank(message = "Text can't be empty")
    private String text;
}
