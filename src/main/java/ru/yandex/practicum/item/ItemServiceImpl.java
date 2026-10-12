package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.booking.Booking;
import ru.yandex.practicum.booking.BookingRepository;
import ru.yandex.practicum.booking.BookingStatus;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.item.dto.*;
import ru.yandex.practicum.request.Request;
import ru.yandex.practicum.request.RequestRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDetailsDto> getItems(Long userId) {
        log.info("Getting items for user with id={}", userId);
        checkUserExists(userId);

        log.debug("Fetching items belonging to user with id={}", userId);
        Map<Long, Item> items = itemRepository.findByOwner_Id(userId).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        log.debug("Found {} items for user with id={}", items.size(), userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        log.debug("Fetching bookings for items owned by user with id={}", userId);
        Map<Long, List<Booking>> bookingsByItem = bookingRepository.findByItem_Owner_Id(userId, sort)
                .stream().collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        log.debug("Bookings grouped for {} items", bookingsByItem.size());

        log.debug("Fetching comments for {} items", items.size());
        Map<Long, List<Comment>> itemsComments = commentRepository.findByItem_IdIn(items.keySet())
                .stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        log.debug("Comments grouped for {} items", itemsComments.size());

        List<ItemDetailsDto> itemsDetailsDto = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        log.debug("Current time for booking comparison: {}", now);

        for (Long itemId : items.keySet()) {
            log.debug("Processing item with id={}", itemId);
            log.debug("Found {} bookings for item with id={}", bookingsByItem
                    .getOrDefault(itemId, List.of()).size(), itemId);

            Booking lastBooking = bookingsByItem.getOrDefault(itemId, List.of()).stream()
                    .filter(booking -> booking.getEnd().isBefore(now)
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .max(Comparator.comparing(Booking::getEnd)).orElse(null);
            Booking nextBooking = bookingsByItem.getOrDefault(itemId, List.of())
                    .stream().filter(booking -> booking.getStart().isAfter(now)
                            && booking.getStatus().equals(BookingStatus.APPROVED))
                    .min(Comparator.comparing(Booking::getStart)).orElse(null);

            List<Comment> comments = itemsComments.getOrDefault(itemId, List.of());
            log.debug("Found {} comments for item with id={}", comments.size(), itemId);

            log.debug("Mapping item with id={} to ItemDetailsDto", itemId);
            ItemDetailsDto itemDetailsDto = ItemMapper
                    .toItemDetailsDto(items.get(itemId), lastBooking, nextBooking, comments);
            itemsDetailsDto.add(itemDetailsDto);
            log.debug("Item with id={} successfully mapped to DTO", itemId);
        }

        log.debug("Fetching items from repository for user with id={}", userId);
        log.info("Successfully retrieved {} items for user with id={}", itemsDetailsDto.size(), userId);
        return itemsDetailsDto;
    }

    @Override
    public ItemDto addNewItem(Long userId, NewItemRequest newItem) {
        log.info("Creating new item for user with id={}", userId);

        log.debug("Getting user with id={}", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("userId", "User with id " + userId + " was not found"));
        log.debug("User with id={} found", userId);

        Request request = null;
        log.debug("Getting request from newItem");
        if (newItem.getRequestId() != null) {
            log.debug("Looking for request with id={}", newItem.getRequestId());
            request = requestRepository.findById(newItem.getRequestId()).orElseThrow(() ->
                    new NotFoundException("requestId", "Request with id "
                            + newItem.getRequestId() + " was not found"));
            log.debug("Request with id={} found", newItem.getRequestId());
        }

        log.debug("Mapping newItem to Item entity");
        Item item = ItemMapper.toItem(newItem, user, request);

        log.debug("Saving new item to repository");
        Item savedItem = itemRepository.save(item);
        log.info("Item successfully created with id={}", savedItem.getId());
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDetailsDto getItemById(Long userId, Long itemId) {
        log.info("Getting item with id={} for user with id={}", itemId, userId);
        checkUserExists(userId);

        log.debug("Fetching item with id={} from repository", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("id", "Item with id " + itemId + " was not found"));

        log.debug("Item found: itemId={}, ownerId={}", itemId, item.getOwner().getId());

        log.debug("Fetching bookings for itemId={} and ownerId={}", itemId, userId);
        List<Booking> itemBookings = bookingRepository.findByItem_IdAndItem_Owner_Id(itemId, userId);
        log.debug("Found {} bookings for itemId={} and ownerId={}", itemBookings.size(), itemId, userId);

        LocalDateTime now = LocalDateTime.now();
        log.debug("Current time for booking comparison: {}", now);
        Booking lastBooking = itemBookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now)
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .max(Comparator.comparing(Booking::getEnd)).orElse(null);

        Booking nextBooking = itemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(now)
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);

        log.debug("Fetching comments for item with id={}", itemId);
        List<Comment> comments = commentRepository.findByItem_IdIn(List.of(itemId));
        log.debug("Found {} comments for item with id={}", comments.size(), itemId);

        log.debug("Mapping item with id={} to ItemDetailsDto", itemId);
        log.info("Successfully retrieved item with id={}", itemId);
        return ItemMapper.toItemDetailsDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateRequest item) {
        log.info("Updating item with id={} for user with id={}", itemId, userId);
        checkUserExists(userId);

        log.debug("Getting old item with id={}", itemId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("id", "Item with id " + itemId + " was not found"));
        log.debug("Old item with id={} found", itemId);

        log.debug("Updating fields of item with id={}", itemId);
        Item updatedItem = ItemMapper.updateItem(oldItem, item);
        log.debug("Fields updated for item with id={}", itemId);

        log.debug("Saving updated item with id={}", itemId);
        Item savedItem = itemRepository.save(updatedItem);
        log.info("Item with id={} successfully updated", itemId);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("Deleting item with id={} for user with id={}", itemId, userId);
        checkUserExists(userId);

        log.debug("Checking existence of item with id={}", itemId);
        itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("id", "Item with id " + itemId + " was not found"));

        log.debug("Item with id={} found, proceeding with deletion", itemId);
        log.debug("Deleting item with id={} from repository", itemId);
        itemRepository.deleteById(itemId);
        log.info("Item with id={} successfully deleted", itemId);
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        log.info("Searching items for user with id={}", userId);
        checkUserExists(userId);

        log.debug("Search query: '{}'", text);
        if (text == null || text.isBlank()) {
            log.debug("Search text is empty, returning empty list");
            return List.of();
        }

        log.debug("Searching available items by text");
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).toList();
    }

    public CommentDto addNewComment(Long userId, Long itemId, NewCommentRequest newComment) {
        log.info("Adding new comment for item with id={}", itemId);

        log.debug("Checking completed approved bookings for userId={} and itemId={}", userId, itemId);
        boolean commentCondition = bookingRepository
                .findUserHasBooking(userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED).isEmpty();
        log.debug("Comment validation result for userId={}, itemId={}: noValidBooking={}", userId,
                itemId, commentCondition);

        if (commentCondition) {
            log.warn("Comment creation rejected: userId={} has no completed approved booking for itemId={}",
                    userId, itemId);
            throw new ValidationException("userId", "User with id=" + userId + " did not book this item");
        }
        log.debug("Comment validation passed for userId={} and itemId={}", userId, itemId);

        log.debug("Getting item with id={} from repository", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("id", "Item with id " + itemId + " was not found"));
        log.debug("Item with id={} found", itemId);

        log.debug("Getting user with id={} from repository", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("userId", "User with id " + userId + " was not found"));
        log.debug("User with id={} found", userId);

        log.debug("Mapping newComment to Comment entity");
        Comment comment = CommentMapper.toComment(newComment, item, user);

        log.debug("Saving new comment to repository");
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment successfully created with id={}", savedComment.getId());
        return CommentMapper.toCommentDto(savedComment);
    }

    private void checkUserExists(Long userId) {
        log.debug("Checking existence of user with id={}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found", userId);
            throw new NotFoundException("userId", "User with id " + userId + " was not found");
        }
        log.debug("User with id={} exists", userId);
    }
}
