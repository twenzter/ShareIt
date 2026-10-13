package ru.yandex.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewCommentRequest {

    @NotBlank(message = "Text can't be empty")
    String text;
}
