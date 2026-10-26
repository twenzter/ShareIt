package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getDescription(),
                request.getCreated());
    }

    public static RequestDetailDto toRequestDetailDto(Request request) {
        List<ItemDto> itemDtos = request.getItems().stream()
                .map(ItemMapper::toItemDto)
                .toList();

        return new RequestDetailDto(request.getId(),
                request.getDescription(),
                itemDtos,
                request.getCreated());
    }

    public static Request toRequest(NewRequest requestDto, User user, LocalDateTime created) {
        return new Request(null,
                requestDto.getDescription(),
                user,
                new ArrayList<>(),
                created);
    }
}
