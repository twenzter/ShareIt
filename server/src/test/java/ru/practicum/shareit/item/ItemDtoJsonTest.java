package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<CommentDto> commentDtoJson;
    private final JacksonTester<ItemDetailsDto> itemDetailsDtoJson;
    private final JacksonTester<ItemDto> itemDtoJson;
    private final JacksonTester<ItemUpdateRequest> itemUpdateRequestJson;
    private final JacksonTester<NewCommentRequest> newCommentRequestJson;
    private final JacksonTester<NewItemRequest> newItemRequestJson;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1L,
                "text",
                1L,
                "name",
                LocalDateTime.now()
        );

        JsonContent<CommentDto> result = commentDtoJson.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(result).hasJsonPath("$.created");
    }

    @Test
    void testItemDetailsDto() throws Exception {
        ItemDetailsDto itemDetailsDto = new ItemDetailsDto(
                1L,
                "name",
                "description",
                true,
                new BookingDto(),
                new BookingDto(),
                List.of()
        );
        JsonContent<ItemDetailsDto> result = itemDetailsDtoJson.write(itemDetailsDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDetailsDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDetailsDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDetailsDto.getAvailable());
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).extractingJsonPathArrayValue("$.comments");
    }

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true
        );

        JsonContent<ItemDto> result = itemDtoJson.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
    }

    @Test
    void testItemUpdateRequest() throws Exception {
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest(
                "name",
                "description",
                true
        );

        JsonContent<ItemUpdateRequest> result = itemUpdateRequestJson.write(itemUpdateRequest);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemUpdateRequest.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemUpdateRequest.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemUpdateRequest.getAvailable());
    }

    @Test
    void testNewCommentRequest() throws Exception {
        NewCommentRequest newCommentRequest = new NewCommentRequest("text");

        JsonContent<NewCommentRequest> result = newCommentRequestJson.write(newCommentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(newCommentRequest.getText());
    }

    @Test
    void testNewItemRequest() throws Exception {
        NewItemRequest newItemRequest = new NewItemRequest(
                "name",
                "description",
                true,
                2L
        );

        JsonContent<NewItemRequest> result = newItemRequestJson.write(newItemRequest);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(newItemRequest.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(newItemRequest.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(newItemRequest.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(2);
    }
}