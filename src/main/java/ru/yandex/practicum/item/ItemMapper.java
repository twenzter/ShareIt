package ru.yandex.practicum.item;

import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemUpdateRequest;

public class ItemMapper {
    public static Item toItem(ItemDto item) {
        return new Item(null,
                item.getName(),
                item.getDescription(),
                null,
                item.getAvailable(),
                item.getRequest());
    }

    public static Item updateItem(Item oldItem, ItemUpdateRequest item) {
        return new Item(oldItem.getId(),
                item.hasName() ? item.getName() : oldItem.getName(),
                item.hasDescription() ? item.getDescription() : oldItem.getDescription(),
                oldItem.getOwner(),
                item.hasAvailable() ? item.getAvailable() : oldItem.getAvailable(),
                oldItem.getRequest());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null);
    }
}
