package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceIntegrationTest {

    private final EntityManager em;
    private final RequestService service;

    @Test
    void addNewUserTest() {
        User owner = makeUser("owner@mail.ru", "name");
        NewRequest newRequest = new NewRequest("description");

        em.persist(owner);
        em.flush();
        em.clear();

        RequestDto created = service.saveNewRequest(owner.getId(), newRequest);

        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.id=:id", Request.class);
        Request requestSql = query.setParameter("id", created.getId()).getSingleResult();

        assertThat(requestSql.getId(), is(created.getId()));
        assertThat(requestSql.getDescription(), is(created.getDescription()));
        assertThat(requestSql.getCreated(), notNullValue());
    }

    @Test
    void getItemByIdTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem("name", owner, true, request);
        request.setItems(List.of(item));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(request);
        em.flush();
        em.clear();

        RequestDetailDto result = service.getRequestById(owner.getId(), request.getId());

        assertThat(result.getId(), is(request.getId()));
        assertThat(result.getDescription(), is(request.getDescription()));
        assertThat(result.getItems().getFirst().getId(), is(item.getId()));
        assertThat(result.getCreated(), notNullValue());
    }

    @Test
    void getAllUserRequestsTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem("name", owner, true, request);
        request.setItems(List.of(item));

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(request);
        em.flush();
        em.clear();

        List<RequestDetailDto> result = service.getAllUserRequests(booker.getId(), 0, 10);

        assertThat(result.getFirst().getId(), is(request.getId()));
        assertThat(result.getFirst().getDescription(), is(request.getDescription()));
        assertThat(result.getFirst().getItems().getFirst().getId(), is(item.getId()));
        assertThat(result.getFirst().getCreated(), notNullValue());
    }

    @Test
    void getAllRequestsTest() {
        User booker = makeUser("booker@mail.ru", "name");

        User owner = makeUser("owner@mail.ru", "name");
        Request request = makeRequest(booker);
        Item item = makeItem("name", owner, true, request);

        em.persist(booker);
        em.persist(owner);
        em.persist(item);
        em.persist(request);
        em.flush();
        em.clear();

        List<RequestDto> result = service.getAllRequests(booker.getId(), 0, 10);

        assertThat(result.getFirst().getId(), is(request.getId()));
        assertThat(result.getFirst().getDescription(), is(request.getDescription()));
        assertThat(result.getFirst().getCreated(), notNullValue());
    }

    private User makeUser(String email, String name) {
        return new User(null, email, name);
    }

    private Item makeItem(String name, User owner, Boolean available, Request request) {
        return new Item(
                null,
                name,
                "description",
                owner,
                available,
                request
        );
    }

    private Request makeRequest(User requestor) {
        return new Request(
                null,
                "description",
                requestor,
                List.of(),
                LocalDateTime.now()
        );
    }
}

