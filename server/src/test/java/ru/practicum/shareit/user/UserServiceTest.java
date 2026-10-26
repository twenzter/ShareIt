package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserByIdSuccessfulTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getEmail(), is(user.getEmail()));
        assertThat(result.getName(), is(user.getName()));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserByIdWithUnknownUserTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveUserSuccessfulTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);
        when(userRepository.save(any()))
                .thenReturn(user);

        NewUserRequest newUserRequest = new NewUserRequest("email@mail.ru", "name");
        UserDto result = userService.saveUser(newUserRequest);

        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getEmail(), is(user.getEmail()));
        assertThat(result.getName(), is(user.getName()));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void saveUserWithAlreadyExistsEmailTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(user);

        NewUserRequest newUserRequest = new NewUserRequest("email@mail.ru", "name");

        assertThrows(DuplicatedDataException.class,
                () -> userService.saveUser(newUserRequest));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsersSuccessfulTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        List<UserDto> result = userService.getAllUsers(0, 10);

        assertThat(result, hasSize(1));
        assertThat(result.getFirst().getId(), is(user.getId()));
        assertThat(result.getFirst().getEmail(), is(user.getEmail()));
        assertThat(result.getFirst().getName(), is(user.getName()));
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void updateUserByIdSuccessfulTest() {
        User user = makeUser(1L, "email@mail.ru", "name");
        User updated = makeUser(1L, "email2@mail.ru", "name2");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(updated);
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("email2@mail.ru", "name2");

        UserDto result = userService.updateUserById(user.getId(), userUpdateRequest);

        assertThat(result.getId(), is(updated.getId()));
        assertThat(result.getEmail(), is(updated.getEmail()));
        assertThat(result.getName(), is(updated.getName()));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserByIdWithUnknownUserIdTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("email2@mail.ru", "name2");

        assertThrows(NotFoundException.class,
                () -> userService.updateUserById(user.getId(), userUpdateRequest));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserByIdWithAlreadyExistsEmailTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(user);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("email2@mail.ru", "name2");

        assertThrows(DuplicatedDataException.class,
                () -> userService.updateUserById(user.getId(), userUpdateRequest));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserByIdSuccessfulTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        userService.deleteUserById(user.getId());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteUserByIdWithWrongUserIdTest() {
        User user = makeUser(1L, "email@mail.ru", "name");

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(user.getId()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
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