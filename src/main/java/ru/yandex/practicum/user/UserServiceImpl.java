
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
        log.info("Getting all users");

        log.debug("Fetching users from repository");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto saveUser(User user) {
        log.info("Creating new user");
        checkUserEmail(user.getEmail());

        log.debug("Saving new user to repository");
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Getting user with id={}", userId);

        log.debug("Fetching user with id={} from repository", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id", "User with id " + userId + " was not found"));

        log.debug("Mapping user with id={} to DTO", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUserById(Long userId, UserUpdateRequest user) {
        log.info("Updating user with id={}", userId);
        checkUserEmail(user.getEmail());

        log.debug("Getting old user with id={}", userId);
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id", "User with id " + userId + " was not found"));

        log.debug("Updating fields of user with id={}", userId);
        User updatedUser = UserMapper.updateUser(oldUser, user);

        log.debug("Saving updated user with id={}", userId);
        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Deleting user with id={}", userId);

        log.debug("Checking existence of user with id={}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User {} does not exist in repository", userId);
            throw new NotFoundException("id", "User with id " + userId + " was not found");
        }

        log.debug("Deleting user with id={} from repository", userId);
        userRepository.deleteById(userId);
        log.info("User with id={} successfully deleted", userId);
    }

    private void checkUserEmail(String email) {
        log.debug("Checking if email already exists in repository");

        if (email != null && userRepository.findByEmail(email) != null) {
            log.warn("Email {} already exists in repository", email);
            throw new DuplicatedDataException("email", "Email already exists in repository");
        }
    }
}
