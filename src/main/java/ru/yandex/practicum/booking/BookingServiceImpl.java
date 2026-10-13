package ru.yandex.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.dto.BookingState;
import ru.yandex.practicum.booking.dto.NewBookingRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.item.ItemRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto addNewBooking(Long userId, NewBookingRequest newBooking) {
        log.info("Adding booking for item with id={}", newBooking.getItemId());

        log.debug("Getting user with id={}", userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("userId", "User with id "
                        + userId + " was not found"));

        log.debug("Getting item with id={}", newBooking.getItemId());
        Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() -> new NotFoundException("itemId", "Item with id "
                        + newBooking.getItemId() + " was not found"));
        if (item.getAvailable() == false) {
            throw new ValidationException("available", "Item is not available");
        }

        log.debug("Mapping newBookingRequest to Booking entity");
        Booking booking = BookingMapper.toBooking(newBooking, item, booker);
        log.debug("Saving new booking to repository");
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking successfully created with id={}", savedBooking.getId());
        return BookingMapper.toBookingDto(savedBooking);
    }

    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        log.info("Update booking status for booking with id={}", bookingId);

        log.debug("Getting booking with id={} from repository", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("bookingId", "Booking with id "
                        + bookingId + " was not found"));
        log.debug("Checking user id is owner id");
        Long ownerId = booking.getItem().getOwner().getId();
        if (!userId.equals(ownerId)) {
            throw new ValidationException("userId", "User is not owner of item");
        }

        log.debug("Setting status by approved={}", approved);
        if (approved.equals(true)) booking.setStatus(BookingStatus.APPROVED);
        else booking.setStatus(BookingStatus.REJECTED);
        log.info("Booking successfully updated status={}", booking.getStatus());
        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Getting booking by id={}", bookingId);
        checkUserExists(userId);

        log.debug("Getting booking with id={} from repository", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("bookingId", "Booking with id "
                        + bookingId + " was not found"));
        log.info("Booking with id={} successfully retrieved", bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getUserBookings(Long bookerId, String state) {
        log.info("Getting user bookings by id={}", bookerId);
        log.debug("Getting booking state");
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("state", "Unknown booking state " + state);
        }
        checkUserExists(bookerId);

        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        log.debug("Getting user bookings from repository");
        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBooker_Id(bookerId, sort);
            case PAST -> bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, now, sort);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, now, sort);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqual(bookerId,
                    now, now, sort);
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, sort);
        };
        log.info("User bookings successfully retrieved");
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    public List<BookingDto> getUserItemsBookings(Long ownerId, String state) {
        log.info("Getting owner items bookings by id={}", ownerId);
        log.debug("Getting booking state");
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("state", "Unknown booking state " + state);
        }
        checkUserExists(ownerId);

        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        log.debug("Getting owner items bookings from repository");
        List<Booking> itemBookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItem_Owner_Id(ownerId, sort);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, now, sort);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, now, sort);
            case CURRENT -> bookingRepository.findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanEqual(ownerId,
                    now, now, sort);
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sort);
        };

        log.info("Owner items bookings successfully retrieved");
        return itemBookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    private void checkUserExists(Long userId) {
        log.debug("Checking user with id={}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("userId", "User with id " + userId + " was not found"));
    }
}
