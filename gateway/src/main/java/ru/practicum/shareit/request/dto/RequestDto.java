package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}
