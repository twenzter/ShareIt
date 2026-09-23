package ru.yandex.practicum.item.dto;

import lombok.Value;

@Value
public class ItemUpdateRequest {
    String name;
    String description;
    Boolean available;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
