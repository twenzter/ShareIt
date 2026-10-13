package ru.yandex.practicum.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.NewBookingRequest;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    @Transactional
    BookingDto addNewBooking(Long userId, NewBookingRequest newBooking);

    @Transactional
    BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long bookerId, String state);

    List<BookingDto> getUserItemsBookings(Long ownerId, String state);
}
