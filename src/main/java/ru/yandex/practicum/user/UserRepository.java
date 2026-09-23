package ru.yandex.practicum.user;

import java.util.Collection;

public interface UserRepository {
    Collection<User> findAll();

    User save(User user);

    User findUser(Long id);

    User updateUser(Long userId, User updUser);

    boolean deleteUser(Long userId);


    boolean isEmailExists(String email);

    boolean isUserNotExists(Long userId);
}