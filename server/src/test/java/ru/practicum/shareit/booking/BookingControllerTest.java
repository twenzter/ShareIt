package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                BookingStatus.APPROVED,
                new UserDto(),
                new ItemDto());

    @Test
    void addNewBookingTest() throws Exception {
        NewBookingRequest newBookingRequest = new NewBookingRequest(1L,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(2));

        when(bookingService.addNewBooking(anyLong(), any(NewBookingRequest.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(newBookingRequest))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class));
    }

    @Test
    void updateStatusTest() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), ItemDto.class));
    }

    @Test
    void getUserBookingsTest() throws  Exception {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class));
    }

    @Test
    void getUserItemsBookingsTest() throws  Exception {
        when(bookingService.getUserItemsBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), ItemDto.class));
    }
}
