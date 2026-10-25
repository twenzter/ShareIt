package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> bookingDtoJson;
    private final JacksonTester<NewBookingRequest> newBookingRequestJson;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2026,10,6,10,22,34),
                LocalDateTime.of(2026,10,8,11,32,55),
                BookingStatus.APPROVED,
                new UserDto(),
                new ItemDto());

        JsonContent<BookingDto> result = bookingDtoJson.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.item");
    }

    @Test
    void testNewBookingRequest() throws Exception {
        NewBookingRequest newBookingRequest = new NewBookingRequest(
                1L,
                LocalDateTime.of(2026,10,6,10,22,34),
                LocalDateTime.of(2026,10,8,11,32,55));

        JsonContent<NewBookingRequest> result = newBookingRequestJson.write(newBookingRequest);

        assertThat(result).doesNotHaveJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(newBookingRequest.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(newBookingRequest.getEnd().toString());
    }
}