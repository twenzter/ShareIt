package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService service;

    @Test
    void addNewBookingTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Item item = makeItem("name", owner, true);

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.flush();
        em.clear();

        NewBookingRequest newBooking = new NewBookingRequest(
                item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        BookingDto created = service.addNewBooking(booker.getId(), newBooking);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id=:id", Booking.class);
        Booking bookingSql = query.setParameter("id", created.getId()).getSingleResult();

        assertThat(bookingSql.getId(), is(created.getId()));
        assertThat(bookingSql.getStart(), notNullValue());
        assertThat(bookingSql.getEnd(), notNullValue());
        assertThat(bookingSql.getItem().getId(), is(created.getItem().getId()));
        assertThat(bookingSql.getBooker().getId(), is(created.getBooker().getId()));
        assertThat(bookingSql.getStatus(), is(created.getStatus()));
    }

    @Test
    void updateBookingStatusTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Item item = makeItem("name", owner, true);

        Booking booking = makeBooking(item, booker);

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.flush();
        em.clear();

        BookingDto updated = service.updateBookingStatus(owner.getId(), booking.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id=:id", Booking.class);
        Booking bookingSql = query.setParameter("id", updated.getId()).getSingleResult();

        assertThat(bookingSql.getId(), is(updated.getId()));
        assertThat(bookingSql.getStart(), notNullValue());
        assertThat(bookingSql.getEnd(), notNullValue());
        assertThat(bookingSql.getItem().getId(), is(item.getId()));
        assertThat(bookingSql.getBooker().getId(), is(booker.getId()));
        assertThat(bookingSql.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void getBookingByIdTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Item item = makeItem("name", owner, true);

        Booking booking = makeBooking(item, booker);

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
        em.flush();
        em.clear();

        BookingDto result = service.getBookingById(owner.getId(), booking.getId());

        assertThat(result.getId(), is(booking.getId()));
        assertThat(result.getStart(), notNullValue());
        assertThat(result.getEnd(), notNullValue());
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        assertThat(result.getStatus(), is(booking.getStatus()));
    }

    @Test
    void getUserBookingsTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Item item1 = makeItem("name1", owner, false);
        Item item2 = makeItem("name2", owner, true);

        Booking booking1 = makeBooking(item1, booker);
        Booking booking2 = makeBooking(item2, booker);

        em.persist(booker);
        em.persist(owner);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.flush();
        em.clear();

        List<BookingDto> ownerBookings = service.getUserBookings(booker.getId(), "ALL", 0, 10);

        assertThat(ownerBookings.size(), is(2));
        assertThat(ownerBookings.stream()
                        .map(BookingDto::getId)
                        .toList(),
                containsInAnyOrder(booking1.getId(), booking2.getId()));
    }

    @Test
    void getUserItemsBookingsTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Item item1 = makeItem("name1", owner, false);
        Item item2 = makeItem("name2", owner, true);

        Booking booking1 = makeBooking(item1, booker);
        Booking booking2 = makeBooking(item2, booker);

        em.persist(booker);
        em.persist(owner);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.flush();
        em.clear();

        List<BookingDto> userItemBookings = service.getUserItemsBookings(owner.getId(), "ALL", 0, 10);

        assertThat(userItemBookings.size(), is(2));
        assertThat(userItemBookings.stream()
                        .map(BookingDto::getId)
                        .toList(),
                containsInAnyOrder(booking1.getId(), booking2.getId()));
    }

    private User makeUser(String email, String name) {
        return new User(null, email, name);
    }

    private Item makeItem(String name, User owner, Boolean available) {
        return new Item(
                null,
                name,
                "description",
                owner,
                available,
                null
        );
    }

    private Booking makeBooking(Item item, User booker) {
        return new Booking(
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING
        );
    }
}

