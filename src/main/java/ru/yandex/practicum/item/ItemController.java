package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemUpdateRequest;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("item controller get items request");
        return ResponseEntity.ok(itemService.getItems(userId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody ItemDto item) {
        log.info("item controller gad item request");
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addNewItem(userId, item));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        log.info("item controller get item by id request");
        return ResponseEntity.ok(itemService.getItemById(userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemUpdateRequest item) {
        log.info("item controller update item request");
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, item));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable(name="itemId") Long itemId) {
        log.info("item controller delete item request");
        itemService.deleteItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam String text) {
        log.info("item controller search items request");
        return ResponseEntity.ok(itemService.search(userId, text));
    }
}