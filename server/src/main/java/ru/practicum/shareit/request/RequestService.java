package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@Transactional(readOnly = true)
public interface RequestService {
    @Transactional
    RequestDto saveNewRequest(Long userId, NewRequest newRequest);

    List<RequestDetailDto> getAllUserRequests(Long userId, Integer from, Integer size);

    List<RequestDto> getAllRequests(Long userId, Integer from, Integer size);

    RequestDetailDto getRequestById(Long userId, Long requestId);
}
