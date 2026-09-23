package ru.yandex.practicum.item;

import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemUpdateRequest;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    boolean deleteItem(Long userId, Long itemId);

    ItemDto getItemById(Long userId, Long itemId);

    ItemDto updateItem(Long userId, Long itemId, ItemUpdateRequest item);

    Collection<ItemDto> search(Long userId, String text);
}
