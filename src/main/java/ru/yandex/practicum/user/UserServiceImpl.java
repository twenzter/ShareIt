package ru.yandex.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.DuplicatedDataException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.dto.UserUpdateRequest;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto saveUser(User user) {
        if (userRepository.isEmailExists(user.getEmail())) {
            log.warn("email {} already exists in repository", user.getEmail());
            throw new DuplicatedDataException("email", "email already exists in repository");
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkUserExists(userId);
        return UserMapper.toUserDto(userRepository.findUser(userId));
    }

    @Override
    public UserDto updateUserById(Long userId, UserUpdateRequest user) {
        checkUserExists(userId);
        if (userRepository.isEmailExists(user.getEmail())) {
            log.warn("email {} already exists in repository", user.getEmail());
            throw new DuplicatedDataException("email", "email already exists in repository");
        }
        log.debug("get old user by id");
        User oldUser = userRepository.findUser(userId);
        log.debug("update old user fields");
        User updatedUser = UserMapper.updateUser(oldUser, user);
        return UserMapper.toUserDto(userRepository.updateUser(userId, updatedUser));
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserExists(userId);
        userRepository.deleteUser(userId);
    }

    private void checkUserExists(Long userId) {
        if (userRepository.isUserNotExists(userId)) {
            log.warn("user with id {} was not found", userId);
            throw new NotFoundException("id", "user with id " + userId + " was not found");
        }
    }
}