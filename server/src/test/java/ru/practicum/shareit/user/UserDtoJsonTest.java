package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<NewUserRequest> newUserRequestJson;
    private final JacksonTester<UserDto> userDtoJson;
    private final JacksonTester<UserUpdateRequest> userUpdateRequestJson;

    @Test
    void testNewUserRequest() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("email@mail.ru", "name");

        JsonContent<NewUserRequest> result = newUserRequestJson.write(newUserRequest);

        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(newUserRequest.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(newUserRequest.getName());
    }

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "email@mail.ru",
                "name"
        );

        JsonContent<UserDto> result = userDtoJson.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
    }

    @Test
    void testUserUpdatedRequest() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("email2@mail.ru", "name2");

        JsonContent<UserUpdateRequest> result = userUpdateRequestJson.write(userUpdateRequest);

        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(userUpdateRequest.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(userUpdateRequest.getName());
    }
}