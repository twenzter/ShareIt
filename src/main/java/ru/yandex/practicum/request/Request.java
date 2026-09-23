package ru.yandex.practicum.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Request {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
