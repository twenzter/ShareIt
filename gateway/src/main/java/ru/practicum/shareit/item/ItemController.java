package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.get(userId, from, size);
    }

    @PostMapping()
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody NewItemRequest newItem) {
        log.info("Creating booking {}, userId={}", newItem, userId);
        return itemClient.add(userId, newItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemUpdateRequest item) {
        log.info("Update item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable(name = "itemId") Long itemId) {
        log.info("Delete item {}, userId={}", itemId, userId);
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam String text,
                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.search(userId, text, from, size);
    }

    //comments
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody NewCommentRequest newComment) {
        log.info("Creating comment {} to item {}, userId={}", newComment, itemId, userId);
        return itemClient.addComment(userId, itemId, newComment);
    }
}