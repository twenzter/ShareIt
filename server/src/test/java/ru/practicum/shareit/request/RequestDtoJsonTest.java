package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestDtoJsonTest {
    private final JacksonTester<NewRequest> newRequestJson;
    private final JacksonTester<RequestDetailDto> requestDetailDtoJson;
    private final JacksonTester<RequestDto> requestDtoJson;

    @Test
    void testNewRequest() throws Exception {
        NewRequest newRequest = new NewRequest("description");

        JsonContent<NewRequest> result = newRequestJson.write(newRequest);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(newRequest.getDescription());
    }

    @Test
    void testRequestDto() throws Exception {
        RequestDto requestDto = new RequestDto(
                1L,
                "description",
                LocalDateTime.now()
        );

        JsonContent<RequestDto> result = requestDtoJson.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(requestDto.getDescription());
        assertThat(result).hasJsonPath("$.created");
    }

    @Test
    void testRequestDetailsDto() throws Exception {
        RequestDetailDto requestDetailDto = new RequestDetailDto(
                1L,
                "description",
                List.of(),
                LocalDateTime.now()
        );

        JsonContent<RequestDetailDto> result = requestDetailDtoJson.write(requestDetailDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(requestDetailDto.getDescription());
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .isEqualTo(requestDetailDto.getItems());
        assertThat(result).hasJsonPath("$.created");
    }
}