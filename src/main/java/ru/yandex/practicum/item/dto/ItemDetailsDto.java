package ru.yandex.practicum.item.dto;

import lombok.Value;
import ru.yandex.practicum.booking.dto.BookingDto;

import java.util.List;

@Value
public class ItemDetailsDto {

    Long id;

    String name;

    String description;

    Boolean available;

    BookingDto lastBooking;

    BookingDto nextBooking;

    List<CommentDto> comments;
}
