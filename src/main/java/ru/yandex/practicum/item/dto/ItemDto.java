package ru.yandex.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ItemDto {
    Long id;
    @NotBlank(message = "name can not be empty")
    String name;
    @NotBlank(message = "description can not be empty")
    String description;
    @NotNull(message = "available status can not be empty")
    Boolean available;
    Long request;
}
