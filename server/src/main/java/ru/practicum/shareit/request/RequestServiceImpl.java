package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDetailDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    public RequestDto saveNewRequest(Long userId, NewRequest newRequest) {
        log.info("Creating new request for user with id={}", userId);
        log.debug("Getting requestor with id={}", userId);
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        log.debug("User with id={} found", userId);

        log.debug("Mapping requestDto to request");
        Request request = RequestMapper.toRequest(newRequest, requestor, LocalDateTime.now());

        log.debug("Saving new request to repository");
        Request savedRequest = requestRepository.save(request);
        log.info("Request successfully created with id={}", savedRequest.getId());
        return RequestMapper.toRequestDto(savedRequest);
    }

    public List<RequestDetailDto> getAllUserRequests(Long userId, Integer from, Integer size) {
        log.info("Creating get requests for user with id={}", userId);
        checkUserExists(userId);

        Pageable page = PageRequest.of(from, size, sort);
        log.debug("Fetching user requests from repository");
        return requestRepository.findByRequestor_Id(userId, page).stream()
                .map(RequestMapper::toRequestDetailDto)
                .toList();
    }

    public List<RequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Creating get all requests");
        checkUserExists(userId);

        Pageable page = PageRequest.of(from, size, sort);
        log.info("Sorting by created field");
        return requestRepository.findAll(page).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    public RequestDetailDto getRequestById(Long userId, Long requestId) {
        log.info("Creating get request with id={}", requestId);
        checkUserExists(userId);

        log.debug("Getting request with id={}", requestId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " was not found"));

        log.debug("Mapping request with id={} to detail DTO", requestId);
        System.out.println(RequestMapper.toRequestDetailDto(request));
        return RequestMapper.toRequestDetailDto(request);
    }

    public void checkUserExists(Long userId) {
        log.debug("Checking user with id={}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
    }
}
