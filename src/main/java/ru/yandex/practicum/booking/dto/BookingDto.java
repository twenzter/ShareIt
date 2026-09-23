package ru.yandex.practicum.booking.dto;

import ru.yandex.practicum.booking.BookingStatus;
import ru.yandex.practicum.item.Item;

import java.time.LocalDateTime;

public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private BookingStatus status;
}
