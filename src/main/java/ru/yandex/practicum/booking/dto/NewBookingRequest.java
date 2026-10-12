package ru.yandex.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    @NotNull(message = "Item id can't be empty")
    private Long itemId;

    @NotNull(message = "Start can't be empty")
    private LocalDateTime start;

    @NotNull(message = "End can't be empty")
    private LocalDateTime end;
}
