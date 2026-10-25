package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void saveNewRequest() {

    }

    @Test
    void getAllUserRequests() {

    }

    @Test
    void getAllRequests() {

    }

    @Test
    void getRequestById() {

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

    private Request makeRequest(User requestor, List<Item> items) {
        return new Request(
                1L,
                "description",
                requestor,
                items,
                LocalDateTime.now()
        );
    }

    private Comment makeComment(Item item, User author) {
        return new Comment(
                1L,
                "text",
                item,
                author,
                LocalDateTime.now()
        );
    }
}