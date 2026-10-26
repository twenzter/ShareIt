package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RequestController.class})
public class RequestControllerTest {
    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final RequestDto requestDto = new RequestDto(
            1L,
            "description",
            LocalDateTime.now()
    );

    private final RequestDetailDto requestDetailDto = new RequestDetailDto(
            1L,
            "description",
            List.of(),
            LocalDateTime.now()
    );

    @Test
    void addNewItemTest() throws Exception {
        NewRequest newRequest = new NewRequest("description");

        when(requestService.saveNewRequest(anyLong(), any(NewRequest.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequest))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDetailDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDetailDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDetailDto.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(requestDetailDto.getItems()), List.class))
                .andExpect(jsonPath("$.created", notNullValue()));
    }

    @Test
    void getAllUserRequestsTest() throws Exception {
        when(requestService.getAllUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDetailDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from","0")
                        .param("size","10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(requestDetailDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDetailDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].items", is(requestDetailDto.getItems()), List.class))
                .andExpect(jsonPath("$[0].created", notNullValue()));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from","0")
                        .param("size","10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].created", notNullValue()));
    }
}
