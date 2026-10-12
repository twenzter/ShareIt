package ru.yandex.practicum.booking;

import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.NewBookingRequest;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.item.ItemMapper;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserMapper;

public class BookingMapper {
    public static Booking toBooking(NewBookingRequest newBooking, Item item, User booker) {
        return new Booking(null,
                newBooking.getStart(),
                newBooking.getEnd(),
                item,
                booker,
                BookingStatus.WAITING);
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()));
    }
}
