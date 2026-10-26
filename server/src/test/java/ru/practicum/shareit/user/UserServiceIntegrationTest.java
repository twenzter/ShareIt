package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void addNewUserTest() {
        NewUserRequest newUserRequest = new NewUserRequest("email@mail.ru", "name");

        UserDto created = service.saveUser(newUserRequest);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id=:id", User.class);
        User userSql = query.setParameter("id", created.getId()).getSingleResult();

        assertThat(userSql.getId(), is(created.getId()));
        assertThat(userSql.getEmail(), is(created.getEmail()));
        assertThat(userSql.getName(), is(created.getName()));
    }

    @Test
    void updateUserTest() {
        User user = makeUser("user@mail.ru", "name");

        em.persist(user);
        em.flush();
        em.clear();

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("email2@mail.ru", "name2");
        UserDto updated = service.updateUserById(user.getId(), userUpdateRequest);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id=:id", User.class);
        User userSql = query.setParameter("id", updated.getId()).getSingleResult();

        assertThat(userSql.getId(), is(updated.getId()));
        assertThat(userSql.getEmail(), is(updated.getEmail()));
        assertThat(userSql.getName(), is(updated.getName()));
    }

    @Test
    void getUserByIdTest() {
        User user = makeUser("user@mail.ru", "name");

        em.persist(user);
        em.flush();
        em.clear();

        UserDto result = service.getUserById(user.getId());

        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getEmail(), is(user.getEmail()));
        assertThat(result.getName(), is(user.getName()));
    }


    @Test
    void getALlUsersTest() {
        User user = makeUser("user@mail.ru", "name");

        em.persist(user);
        em.flush();
        em.clear();

        List<UserDto> result = service.getAllUsers(0, 10);

        assertThat(result.getFirst().getId(), is(user.getId()));
        assertThat(result.getFirst().getEmail(), is(user.getEmail()));
        assertThat(result.getFirst().getName(), is(user.getName()));
    }

    @Test
    void deleteUserTest() {
        User user = makeUser("user@mail.ru", "name");

        em.persist(user);
        em.flush();
        em.clear();

        service.deleteUserById(user.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id=:id", User.class);
        assertThrows(NoResultException.class,
                () -> query.setParameter("id", user.getId()).getSingleResult());
    }

    private User makeUser(String email, String name) {
        return new User(null, email, name);
    }
}

