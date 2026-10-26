package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addNewBookingSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    booking.setId(100L);
                    return booking;
                });

        NewBookingRequest newBooking = new NewBookingRequest(
                item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        BookingDto result = bookingService.addNewBooking(1L, newBooking);

        assertThat(result.getId(), is(100L));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        assertThat(result.getStatus(), is(BookingStatus.WAITING));

        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(10L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addNewBookingWithWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NewBookingRequest newBooking = new NewBookingRequest(
                10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(1L, newBooking));

        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, never()).findById(10L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addNewBookingWithWrongItemTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L))
                .thenReturn(Optional.empty());

        NewBookingRequest newBooking = new NewBookingRequest(
                item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(1L, newBooking));

        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(10L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addNewBookingWithUnavailableItemTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, false);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L))
                .thenReturn(Optional.of(item));

        NewBookingRequest newBooking = new NewBookingRequest(
                item.getId(),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(ValidationException.class,
                () -> bookingService.addNewBooking(1L, newBooking));

        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(10L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    void updateBookingStatusSuccessfulAcceptTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.updateBookingStatus(2L, 30L, true);

        assertThat(result.getId(), is(30L));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        assertThat(result.getStatus(), is(BookingStatus.APPROVED));

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingStatusSuccessfulRejectTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.updateBookingStatus(2L, 30L, false);

        assertThat(result.getId(), is(30L));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        assertThat(result.getStatus(), is(BookingStatus.REJECTED));

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingStatusWithUnknownBookingIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(2L, 30L, true));

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateBookingStatusWithWrongOwnerTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, false);
        Booking booking = makeBooking(30L, item, booker);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.updateBookingStatus(1L, 30L, true));

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingByIdSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 30L);

        assertThat(result.getId(), is(30L));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
        assertThat(result.getStatus(), is(BookingStatus.WAITING));

        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingByIdWithUnknownUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 30L));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void getBookingByIdWithUnknownIdTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 30L));

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserBookingsSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, false);

        Booking booking = makeBooking(30L, item, booker);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserBookings(1L, "ALL", 0, 10);

        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(30L));
        assertThat(result.getFirst().getItem().getId(), is(item.getId()));
        assertThat(result.getFirst().getBooker().getId(), is(booker.getId()));
        assertThat(result.getFirst().getStatus(), is(BookingStatus.WAITING));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithUnknownUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getUserBookings(1L, "ALL", 0, 10));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getUserBookingsWithUnknownStateTest() {
        assertThrows(ValidationException.class,
                () -> bookingService.getUserBookings(1L, "WTF", 0, 10));

        verify(userRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).findByBooker_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getUserItemsBookingsSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, false);

        Booking booking = makeBooking(30L, item, booker);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getUserItemsBookings(1L, "ALL", 0, 10);

        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(30L));
        assertThat(result.getFirst().getItem().getId(), is(item.getId()));
        assertThat(result.getFirst().getBooker().getId(), is(booker.getId()));
        assertThat(result.getFirst().getStatus(), is(BookingStatus.WAITING));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItem_Owner_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getUserItemsBookingsWithUnknownUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getUserItemsBookings(1L, "ALL", 0, 10));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findByItem_Owner_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getUserItemsBookingsWithUnknownStateTest() {
        assertThrows(ValidationException.class,
                () -> bookingService.getUserItemsBookings(1L, "WTF", 0, 10));

        verify(userRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).findByItem_Owner_Id(anyLong(), any(Pageable.class));
    }

    private User makeUser(Long id, String email, String name) {
        return new User(id, email, name);
    }

    private Item makeItem(Long id,String name, User owner, Boolean available) {
        return new Item(id,
                name,
                "description",
                owner,
                available,
                null);
    }

    private Booking makeBooking(Long id, Item item, User booker) {
        return new Booking(id,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);
    }
}