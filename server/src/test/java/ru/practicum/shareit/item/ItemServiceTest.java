package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
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
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemsSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(userRepository.existsById(2L))
                .thenReturn(true);
        when(itemRepository.findByOwner_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));
        when(bookingRepository.findByItem_Owner_Id(anyLong(), any(Sort.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItem_IdIn(any()))
                .thenReturn(List.of());

        List<ItemDetailsDto> result = itemService.getItems(2L, 0, 10);
        System.out.println(result);

        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(item.getId()));
        assertThat(result.getFirst().getName(), is(item.getName()));
        assertThat(result.getFirst().getDescription(), is(item.getDescription()));
        assertThat(result.getFirst().getAvailable(), is(item.getAvailable()));
        assertThat(result.getFirst().getComments(), is(List.of()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByOwner_Id(anyLong(), any(Pageable.class));
        verify(bookingRepository, times(1)).findByItem_Owner_Id(anyLong(), any(Sort.class));
        verify(commentRepository, times(1)).findByItem_IdIn(any());
    }

    @Test
    void getItemsWithUnknownUserTest() {
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(2L))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                 () -> itemService.getItems(2L, 0, 10));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).findByOwner_Id(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).findByItem_Owner_Id(anyLong(), any(Sort.class));
        verify(commentRepository, never()).findByItem_IdIn(any());
    }

    @Test
    void getItemsWithEmptyItemsListTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(userRepository.existsById(2L))
                .thenReturn(true);
        when(itemRepository.findByOwner_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of());
        when(bookingRepository.findByItem_Owner_Id(anyLong(), any(Sort.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItem_IdIn(any()))
                .thenReturn(List.of());

        List<ItemDetailsDto> result = itemService.getItems(2L, 0, 10);
        System.out.println(result);

        assertThat(result.size(), is(0));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByOwner_Id(anyLong(), any(Pageable.class));
        verify(bookingRepository, times(1)).findByItem_Owner_Id(anyLong(), any(Sort.class));
        verify(commentRepository, times(1)).findByItem_IdIn(any());
    }

    @Test
    void addNewItemSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Request request = makeRequest(booker, List.of(item));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        NewItemRequest newItemRequest = new NewItemRequest(
                "name",
                "description",
                true,
                2L
        );
        ItemDto result = itemService.addNewItem(2L, newItemRequest);
        System.out.println(result);

        assertThat(result.getId(), is(item.getId()));
        assertThat(result.getName(), is(item.getName()));
        assertThat(result.getDescription(), is(item.getDescription()));
        assertThat(result.getAvailable(), is(item.getAvailable()));

        verify(userRepository, times(1)).findById(2L);
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addNewItemWithUnknownUserTest() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        NewItemRequest newItemRequest = new NewItemRequest(
                "name",
                "description",
                true,
                2L
        );
        assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(2L, newItemRequest));

        verify(userRepository, times(1)).findById(2L);
        verify(requestRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addNewItemWithUnknownRequestTest() {
        User owner = makeUser(2L,"owner@mail.ru", "name");

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NewItemRequest newItemRequest = new NewItemRequest(
                "name",
                "description",
                true,
                2L
        );
        assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(2L, newItemRequest));

        verify(userRepository, times(1)).findById(2L);
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteItemSuccessfulTest() {
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(2L))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.deleteItem(owner.getId(), item.getId());

        verify(userRepository, times(1)).existsById(2L);
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteItemWithUnknownUserTest() {
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(2L))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(owner.getId(), item.getId()));

        verify(userRepository, times(1)).existsById(2L);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void deleteItemWithUnknownItemTest() {
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(2L))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(owner.getId(), item.getId()));

        verify(userRepository, times(1)).existsById(2L);
        verify(itemRepository, times(1)).findById(anyLong());

    }

    @Test
    void getItemByIdSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndItem_Owner_Id(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItem_IdIn(any()))
                .thenReturn(List.of());

        ItemDetailsDto result = itemService.getItemById(booker.getId(), item.getId());

        assertThat(result.getId(), is(item.getId()));
        assertThat(result.getName(), is(item.getName()));
        assertThat(result.getDescription(), is(item.getDescription()));
        assertThat(result.getAvailable(), is(item.getAvailable()));
        assertThat(result.getComments(), is(List.of()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItem_IdAndItem_Owner_Id(anyLong(), anyLong());
        verify(commentRepository, times(1)).findByItem_IdIn(any());
    }

    @Test
    void getItemByIdWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(booker.getId(), item.getId()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).findByItem_IdAndItem_Owner_Id(anyLong(), anyLong());
        verify(commentRepository, never()).findByItem_IdIn(any());
    }

    @Test
    void getItemByIdWithUnknownItemTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(booker.getId(), item.getId()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findByItem_IdAndItem_Owner_Id(anyLong(), anyLong());
        verify(commentRepository, never()).findByItem_IdIn(any());
    }

    @Test
    void updateItemSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Item updatedItem = new Item(10L, "name", "other", owner, true, null);

        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest(
                "name",
                "other",
                true
        );
        ItemDto result = itemService.updateItem(booker.getId(), item.getId(), itemUpdateRequest);

        assertThat(result.getId(), is(updatedItem.getId()));
        assertThat(result.getName(), is(updatedItem.getName()));
        assertThat(result.getDescription(), is(updatedItem.getDescription()));
        assertThat(result.getAvailable(), is(updatedItem.getAvailable()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(1L))
                .thenReturn(false);

        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest(
                "name",
                "other",
                true
        );
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(booker.getId(), item.getId(), itemUpdateRequest));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemWithUnknownItemTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest(
                "name",
                "other",
                true
        );
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(booker.getId(), item.getId(), itemUpdateRequest));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void searchSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);

        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.search(booker.getId(), "name", 0, 10);

        assertThat(result.getFirst().getId(), is(item.getId()));
        assertThat(result.getFirst().getName(), is(item.getName()));
        assertThat(result.getFirst().getDescription(), is(item.getDescription()));
        assertThat(result.getFirst().getAvailable(), is(item.getAvailable()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    void searchWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        when(userRepository.existsById(1L))
                .thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> itemService.search(booker.getId(), "name", 0, 10));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }


    @Test
    void searchWithEmptyItemListTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");

        when(userRepository.existsById(1L))
                .thenReturn(true);
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(List.of());

        List<ItemDto> result = itemService.search(booker.getId(), "name", 0, 10);

        assertThat(result.size(), is(0));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    void addNewCommentSuccessfulTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);
        Comment comment = makeComment(item, booker);

        when(bookingRepository
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        NewCommentRequest newCommentRequest = new NewCommentRequest("text");
        CommentDto result = itemService.addNewComment(booker.getId(), item.getId(), newCommentRequest);

        assertThat(result.getId(), is(comment.getId()));
        assertThat(result.getText(), is(comment.getText()));
        assertThat(result.getItemId(), is(comment.getItem().getId()));
        assertThat(result.getAuthorName(), is(comment.getAuthor().getName()));
        assertThat(result.getCreated(), notNullValue());

        verify(bookingRepository, times(1))
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addNewCommentWithEmptyBookingsListTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Comment comment = makeComment(item, booker);

        when(bookingRepository
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of());

        NewCommentRequest newCommentRequest = new NewCommentRequest("text");
        assertThrows(ValidationException.class,
                () -> itemService.addNewComment(booker.getId(), item.getId(), newCommentRequest));

        verify(bookingRepository, times(1))
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class));
        verify(itemRepository, never()).findById(anyLong());
        verify(userRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addNewCommentWithUnknownItemTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);

        when(bookingRepository
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NewCommentRequest newCommentRequest = new NewCommentRequest("text");
        assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(booker.getId(), item.getId(), newCommentRequest));

        verify(bookingRepository, times(1))
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addNewCommentWithUnknownUserTest() {
        User booker = makeUser(1L, "booker@mail.ru", "name");
        User owner = makeUser(2L,"owner@mail.ru", "name");
        Item item = makeItem(10L,"name", owner, true);
        Booking booking = makeBooking(30L, item, booker);
        Comment comment = makeComment(item, booker);

        when(bookingRepository
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NewCommentRequest newCommentRequest = new NewCommentRequest("text");
        assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(booker.getId(), item.getId(), newCommentRequest));

        verify(bookingRepository, times(1))
                .findUserHasBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(BookingStatus.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
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