package ru.yandex.practicum.item;

import ru.yandex.practicum.item.dto.ItemDto;

import java.util.Collection;

public interface ItemRepository {

    Collection<Item> findByUserId(Long userId);

    Item save(Item item);

    Item findByItemId(Long itemId);

    Item updateItem(Long itemId, Item updItem);

    boolean deleteByItemId(Long itemId);

    Collection<Item> search(String text);

    boolean isItemNotExists(Long itemId);
}