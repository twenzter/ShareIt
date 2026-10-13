package ru.yandex.practicum.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Getting user bookings
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqual(Long bookerId, LocalDateTime start,
                                                                             LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    //Getting user items bookings
    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanEqual(Long ownerId, LocalDateTime start,
                                                                                 LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    //Getting item with bookings
    List<Booking> findByItem_IdAndItem_Owner_Id(Long itemId, Long ownerId);

    //For comments checking
    @Query("select b " +
            "from Booking b " +
            "JOIN b.item i JOIN b.booker u " +
            "where u.id = ?1 AND i.id = ?2 AND b.end < ?3 AND b.status = ?4")
    List<Booking> findUserHasBooking(Long bookerId, Long itemId, LocalDateTime now, BookingStatus status);
}
