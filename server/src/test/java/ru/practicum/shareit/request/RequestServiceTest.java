package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    void saveNewRequestSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(requestRepository.save(any()))
                .thenReturn(request);

        NewRequest newRequest = new NewRequest("description");
        RequestDto result = requestService.saveNewRequest(booker.getId(), newRequest);

        assertThat(result.getId(), is(request.getId()));
        assertThat(result.getDescription(), is(request.getDescription()));
        assertThat(result.getCreated(), notNullValue());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void saveNewRequestWithUnknownUserIdTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NewRequest newRequest = new NewRequest("description");
        assertThrows(NotFoundException.class,
                () -> requestService.saveNewRequest(booker.getId(), newRequest));

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getAllUserRequestsSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(requestRepository.findByRequestor_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));

        List<RequestDetailDto> result = requestService.getAllUserRequests(booker.getId(), 0, 10);

        assertThat(result.getFirst().getId(), is(request.getId()));
        assertThat(result.getFirst().getDescription(), is(request.getDescription()));
        assertThat(result.getFirst().getItems().getFirst().getId(), is(item.getId()));
        assertThat(result.getFirst().getCreated(), notNullValue());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1))
                .findByRequestor_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllUserRequestsWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getAllUserRequests(booker.getId(), 0, 10));

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never())
                .findByRequestor_Id(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllRequestsSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(requestRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(request)));

        List<RequestDto> result = requestService.getAllRequests(booker.getId(), 0, 10);

        assertThat(result.getFirst().getId(), is(request.getId()));
        assertThat(result.getFirst().getDescription(), is(request.getDescription()));
        assertThat(result.getFirst().getCreated(), notNullValue());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllRequestsWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getAllUserRequests(booker.getId(), 0, 10));

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getRequestByIdSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        RequestDetailDto result = requestService.getRequestById(booker.getId(), request.getId());

        assertThat(result.getId(), is(request.getId()));
        assertThat(result.getDescription(), is(request.getDescription()));
        assertThat(result.getItems().getFirst().getId(), is(item.getId()));
        assertThat(result.getCreated(), notNullValue());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getRequestByIdWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(booker.getId(), request.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestByIdWithUnknownRequestIdTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        User owner = makeUser(2L, "owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem(10L, "name", owner, true, request);
        request.setItems(List.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(booker.getId(), request.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    private User makeUser(Long id, String email, String name) {
        return new User(id, email, name);
    }

    private Item makeItem(Long id,String name, User owner, Boolean available, Request request) {
        return new Item(id,
                name,
                "description",
                owner,
                available,
                request);
    }

    private Booking makeBooking(Long id, Item item, User booker) {
        return new Booking(id,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                booker,
                BookingStatus.WAITING);
    }

    private Request makeRequest(User requestor) {
        return new Request(
                1L,
                "description",
                requestor,
                List.of(),
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