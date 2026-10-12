package ru.yandex.practicum.item;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.item.dto.*;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    List<ItemDetailsDto> getItems(Long userId);

    @Transactional
    ItemDto addNewItem(Long userId, NewItemRequest newItem);

    @Transactional
    void deleteItem(Long userId, Long itemId);

    ItemDetailsDto getItemById(Long userId, Long itemId);

    @Transactional
    ItemDto updateItem(Long userId, Long itemId, ItemUpdateRequest item);

    List<ItemDto> search(Long userId, String text);

    @Transactional
    CommentDto addNewComment(Long userId, Long itemId, NewCommentRequest newComment);
}
