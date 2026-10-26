package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            1L,
            "email@mail.ru",
            "name"
    );

    @Test
    void addNewUserTest() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("email@mail.ru", "name");

        when(userService.saveUser(any(NewUserRequest.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void updateItemTest() throws Exception {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("email2@mail.ru", "name2");
        UserDto updatedUserDto = new UserDto(
                1L,
                "email2@mail.ru",
                "name2"
        );

        when(userService.updateUserById(anyLong(), any()))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userUpdateRequest))
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName()), String.class));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers(anyInt(), anyInt()))
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName()), String.class));
    }
}
