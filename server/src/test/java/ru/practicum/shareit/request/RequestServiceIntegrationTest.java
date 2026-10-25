package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceIntegrationTest {

    private final EntityManager em;
    private final RequestService service;

//    @Test
//    void addNewItemTest() {
//        User owner = makeUser("owner@mail.ru", "name");
//
//        em.persist(owner);
//        em.flush();
//        em.clear();
//
//        NewItemRequest newItemRequest = new NewItemRequest(
//                "name",
//                "description",
//                true,
//                null
//        );
//
//        ItemDto created = service.addNewItem(owner.getId(), newItemRequest);
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id=:id", Item.class);
//        Item itemSql = query.setParameter("id", created.getId()).getSingleResult();
//
//        assertThat(itemSql.getId(), is(created.getId()));
//        assertThat(itemSql.getDescription(), is(created.getDescription()));
//        assertThat(itemSql.getAvailable(), is(created.getAvailable()));
//    }
//
//    @Test
//    void updateItemTest() {
//        User owner = makeUser("owner@mail.ru", "name");
//        Item item = makeItem("name", owner, true);
//
//        em.persist(owner);
//        em.persist(item);
//        em.flush();
//        em.clear();
//
//        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest(
//                "name",
//                "other",
//                true
//        );
//
//        ItemDto created = service.updateItem(owner.getId(), item.getId(), itemUpdateRequest);
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id=:id", Item.class);
//        Item itemSql = query.setParameter("id", created.getId()).getSingleResult();
//
//        assertThat(itemSql.getId(), is(created.getId()));
//        assertThat(itemSql.getDescription(), is(created.getDescription()));
//        assertThat(itemSql.getAvailable(), is(created.getAvailable()));
//    }
//
//    @Test
//    void getItemByIdTest() {
//        User owner = makeUser("owner@mail.ru", "name");
//        Item item = makeItem("name", owner, true);
//
//        em.persist(owner);
//        em.persist(item);
//        em.flush();
//        em.clear();
//
//        ItemDetailsDto result = service.getItemById(owner.getId(), item.getId());
//
//        assertThat(result.getId(), is(item.getId()));
//        assertThat(result.getName(), is(item.getName()));
//        assertThat(result.getDescription(), is(item.getDescription()));
//        assertThat(result.getAvailable(), is(item.getAvailable()));
//        assertThat(result.getComments(), is(List.of()));
//    }
//
//
//    @Test
//    void getItemsTest() {
//        User owner = makeUser("owner@mail.ru", "name");
//        Item item = makeItem("name", owner, true);
//
//        em.persist(owner);
//        em.persist(item);
//        em.flush();
//        em.clear();
//
//        List<ItemDetailsDto> result = service.getItems(owner.getId(), 0, 10);
//
//        assertThat(result.getFirst().getId(), is(item.getId()));
//        assertThat(result.getFirst().getName(), is(item.getName()));
//        assertThat(result.getFirst().getDescription(), is(item.getDescription()));
//        assertThat(result.getFirst().getAvailable(), is(item.getAvailable()));
//        assertThat(result.getFirst().getComments(), is(List.of()));
//    }
//
//    @Test
//    void deleteItemTest() {
//        User owner = makeUser("owner@mail.ru", "name");
//        Item item = makeItem("name", owner, true);
//
//        em.persist(owner);
//        em.persist(item);
//        em.flush();
//        em.clear();
//
//        service.deleteItem(owner.getId(), item.getId());
//
//        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id=:id", Item.class);
//        assertThrows(NoResultException.class,
//                () -> query.setParameter("id", item.getId()).getSingleResult());
//    }
//
//    @Test
//    void searchTest() {
//        User owner = makeUser("owner@mail.ru", "name");
//        Item item = makeItem("name", owner, true);
//
//        em.persist(owner);
//        em.persist(item);
//        em.flush();
//        em.clear();
//
//        List<ItemDto> result = service.search(owner.getId(), "name", 0, 10);
//
//        assertThat(result.getFirst().getId(), is(item.getId()));
//        assertThat(result.getFirst().getName(), is(item.getName()));
//        assertThat(result.getFirst().getDescription(), is(item.getDescription()));
//        assertThat(result.getFirst().getAvailable(), is(item.getAvailable()));
//    }
//
//    @Test
//    void addNewCommentTest() {
//        User booker = makeUser("booker@mail.ru", "name");
//
//        User owner = makeUser("owner@mail.ru", "name");
//        Item item = makeItem("name", owner, true);
//        Booking booking = makeBooking(item, booker);
//
//        em.persist(booker);
//        em.persist(owner);
//        em.persist(item);
//        em.persist(booking);
//        em.flush();
//        em.clear();
//
//        NewCommentRequest newCommentRequest = new NewCommentRequest("text");
//        CommentDto result = service.addNewComment(booker.getId(), item.getId(), newCommentRequest);
//
//        assertThat(result.getId(), notNullValue());
//        assertThat(result.getText(), is("text"));
//        assertThat(result.getItemId(), is(item.getId()));
//        assertThat(result.getAuthorName(), is(booker.getName()));
//        assertThat(result.getCreated(), notNullValue());
//    }
//
//    private User makeUser(String email, String name) {
//        return new User(null, email, name);
//    }
//
//    private Item makeItem(String name, User owner, Boolean available) {
//        return new Item(
//                null,
//                name,
//                "description",
//                owner,
//                available,
//                null
//        );
//    }
//
//    private Booking makeBooking(Item item, User booker) {
//        return new Booking(
//                null,
//                LocalDateTime.now().minusHours(4),
//                LocalDateTime.now().minusHours(2),
//                item,
//                booker,
//                BookingStatus.APPROVED
//        );
//    }
}

