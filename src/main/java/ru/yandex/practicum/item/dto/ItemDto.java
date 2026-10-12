package ru.yandex.practicum.item.dto;

import lombok.Value;

@Value
public class ItemDto {

    Long id;

    String name;

    String description;

    Boolean available;
}
