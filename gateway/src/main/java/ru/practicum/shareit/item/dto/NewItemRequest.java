package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewItemRequest {
    @NotBlank(message = "name can not be empty")
    private String name;

    @NotBlank(message = "description can not be empty")
    private String description;

    @NotNull(message = "available status can not be empty")
    private Boolean available;

    private Long requestId;
}
