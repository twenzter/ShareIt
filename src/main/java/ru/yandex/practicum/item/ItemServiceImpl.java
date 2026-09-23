package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemUpdateRequest;
import ru.yandex.practicum.user.UserRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        checkUserExists(userId);
        return itemRepository.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        checkUserExists(userId);
        log.debug("get item from itemDto");
        Item item = ItemMapper.toItem(itemDto);
        log.debug("set owner to item");
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        checkUserExists(userId);
        checkItemExists(itemId);
        return ItemMapper.toItemDto(itemRepository.findByItemId(itemId));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateRequest item) {
        checkUserExists(userId);
        checkItemExists(itemId);
        log.debug("get old item by id");
        Item oldItem = itemRepository.findByItemId(itemId);
        log.debug("update item fields");
        Item updatedItem = ItemMapper.updateItem(oldItem, item);
        return ItemMapper.toItemDto(itemRepository.updateItem(itemId, updatedItem));
    }

    @Override
    public boolean deleteItem(Long userId, Long itemId) {
        checkUserExists(userId);
        checkItemExists(itemId);
        return itemRepository.deleteByItemId(itemId);
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        checkUserExists(userId);
        log.debug("format source text");
        String comparedText = text.trim().toLowerCase();
        if (comparedText.isBlank()) {
            return List.of();
        }
        return itemRepository.search(comparedText).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private void checkItemExists(Long itemId) {
        if (itemRepository.isItemNotExists(itemId)) {
            log.warn("item with id {} was not found", itemId);
            throw new NotFoundException("id", "item with id " + itemId + " was not found");
        }
    }

    private void checkUserExists(Long userId) {
        if (userRepository.isUserNotExists(userId)) {
            log.warn("user with id {} was not found", userId);
            throw new NotFoundException("id", "user with id " + userId + " was not found");
        }
    }
}
