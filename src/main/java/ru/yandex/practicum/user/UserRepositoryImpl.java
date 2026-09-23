package ru.yandex.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("get all users");
        return users.values();
    }

    @Override
    public User save(User user) {
        log.debug("set id to user");
        user.setId(generateId());
        log.info("save new user");
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUser(Long id) {
        log.info("get user by id");
        return users.get(id);
    }

    @Override
    public User updateUser(Long userId, User updUser) {
        log.info("replace old user to update user");
        users.replace(userId, updUser);
        return updUser;
    }

    @Override
    public boolean deleteUser(Long userId) {
        log.info("delete user");
        return users.remove(userId) != null;
    }


    @Override
    public boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isUserNotExists(Long userId) {
        return ! users.containsKey(userId);
    }

    private Long generateId() {
        log.debug("generate unique id");
        Long maxId = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return ++maxId;
    }

}
