package ru.yandex.practicum.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.dto.UserUpdateRequest;

import java.util.List;

public class UserServiceTest {
    UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Test
    void getAllUsersTest() {
        User user1 = getUser(1L, "email@mail.ru", "name");
        User user2 = getUser(2L, "email2@mail.ru", "name2");
        userService.saveUser(user1);
        userService.saveUser(user2);
        assertThat(userService.getAllUsers()).isEqualTo(List.of(UserMapper.toUserDto(user1),
                UserMapper.toUserDto(user2)));
    }

    @Test
    void getAllUsersWithZeroResultsTest() {
        assertThat(userService.getAllUsers()).isEqualTo(List.of());
    }

    @Test
    void saveUserTest() {
        User user1 = getUser(1L, "email@mail.ru", "name");
        userService.saveUser(user1);
        assertThat(userService.getAllUsers()).isEqualTo(List.of(UserMapper.toUserDto(user1)));
    }

    @Test
    void getUserByIdTest() {
        User user1 = getUser(1L, "email@mail.ru", "name");
        userService.saveUser(user1);
        assertThat(userService.getUserById(1L)).isEqualTo(UserMapper.toUserDto(user1));
    }

    @Test
    void getUserByUnknownIdTest() {
        try {
            userService.getUserById(2L);
            Assertions.fail();
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
        }
    }

    @Test
    void updateUserByIdTest() {
        User user1 = getUser(1L, "email@mail.ru", "name");
        userService.saveUser(user1);
        userService.updateUserById(1L, new UserUpdateRequest("updEmail@mail.ru", "updName"));
        User updatedUser = new User(1L, "updEmail@mail.ru", "updName");
        assertThat(userService.getUserById(1L)).isEqualTo(UserMapper.toUserDto(updatedUser));
    }

    @Test
    void updateUserByUnknownIdTest() {
        User user1 = getUser(1L, "email@mail.ru", "name");
        userService.saveUser(user1);
        try {
            userService.updateUserById(2L, new UserUpdateRequest("updEmail@mail.ru", "updName"));
            Assertions.fail();
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
        }
    }

    @Test
    void deleteUserByIdTest() {
        User user1 = getUser(1L, "email@mail.ru", "name");
        userService.saveUser(user1);
        userService.deleteUserById(1L);
        try {
            userService.getUserById(1L);
            Assertions.fail();
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("user with id 1 was not found");
        }
    }

    @Test
    void deleteUserByUnknownIdTest() {
        try {
            userService.deleteUserById(2L);
            Assertions.fail();
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("user with id 2 was not found");
        }
    }

    User getUser(Long id, String email, String name) {
        return new User(id, email, name);
    }
}
