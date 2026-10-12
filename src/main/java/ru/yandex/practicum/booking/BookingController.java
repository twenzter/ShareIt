package ru.yandex.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.NewBookingRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody NewBookingRequest newBooking) {
        log.info("Booking controller add booking request");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addNewBooking(userId, newBooking));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long bookingId,
                                                   @RequestParam Boolean approved) {
        log.info("Booking controller update status request");
        return ResponseEntity.ok(bookingService.updateBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Booking controller get booking request");
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(defaultValue = "ALL") String state) {
        log.info("Booking controller get user bookings request");
        return ResponseEntity.ok(bookingService.getUserBookings(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Booking controller get user items bookings request");
        return ResponseEntity.ok(bookingService.getUserItemsBookings(userId, state));
    }


}
