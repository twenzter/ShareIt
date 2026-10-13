package ru.yandex.practicum.item;

import ru.yandex.practicum.booking.Booking;
import ru.yandex.practicum.booking.BookingMapper;
import ru.yandex.practicum.item.dto.ItemDetailsDto;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemUpdateRequest;
import ru.yandex.practicum.item.dto.NewItemRequest;
import ru.yandex.practicum.request.Request;
import ru.yandex.practicum.user.User;

import java.util.List;

public class ItemMapper {
    public static Item toItem(ItemDto item, User owner, Request request) {
        return new Item(null,
                item.getName(),
                item.getDescription(),
                owner,
                item.getAvailable(),
                request);
    }

    public static Item toItem(NewItemRequest item, User owner, Request request) {
        return new Item(null,
                item.getName(),
                item.getDescription(),
                owner,
                item.getAvailable(),
                request);
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
                item.getAvailable());
    }

    public static ItemDetailsDto toItemDetailsDto(Item item, Booking lastBooking,
                                                  Booking nextBooking, List<Comment> comments) {
        return new ItemDetailsDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking != null ? BookingMapper.toBookingDto(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDto(nextBooking) : null,
                comments.stream().map(CommentMapper::toCommentDto).toList());
    }
}
