package ru.yandex.practicum.item.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentDto {

    Long id;

    String text;

    Long itemId;

    String authorName;

    LocalDateTime created;
}
