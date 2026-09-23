package ru.yandex.practicum.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long owner;
    private Boolean available;
    private Long request;
}