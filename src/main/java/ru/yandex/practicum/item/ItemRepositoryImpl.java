package ru.yandex.practicum.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> findByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    @Override
    public Item save(Item item) {
        log.debug("set id to item");
        item.setId(generateId());
        log.info("add new item");
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findByItemId(Long itemId) {
        log.info("get item by id");
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Long itemId, Item updItem) {
        log.info("replace old item to updated item");
        items.replace(itemId, updItem);
        return updItem;
    }

    @Override
    public Collection<Item> search(String text) {
        log.info("search items");
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                        && item.getAvailable().equals(true))
                .toList();
    }


    @Override
    public boolean deleteByItemId(Long itemId) {
        log.info("delete item");
        return items.remove(itemId) != null;
    }

    @Override
    public boolean isItemNotExists(Long itemId) {
        return ! items.containsKey(itemId);
    }

    private Long generateId() {
        log.debug("generate unique id");
        Long maxId = items.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return ++maxId;
    }
}
