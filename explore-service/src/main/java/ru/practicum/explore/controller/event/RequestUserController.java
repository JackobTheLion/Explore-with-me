package ru.practicum.explore.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.participation.ParticipationRequestDto;
import ru.practicum.explore.service.EventService;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
@Validated
public class RequestUserController {
    private final EventService eventService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable @Min(1) Long userId,
                                                           @RequestParam @Min(1) Long eventId) {
        log.info("Adding participation request from user id {} to event id {}.", userId, eventId);
        ParticipationRequestDto savedRequest = eventService.addRequest(userId, eventId);
        log.info("Request added: {}.", savedRequest);
        return savedRequest;
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable @Min(1) Long userId) {
        log.info("Looking for requests of user id {}.", userId);
        List<ParticipationRequestDto> userRequests = eventService.getUserRequests(userId);
        log.info("Requests found: {}.", userRequests);
        return userRequests;
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Min(1) Long userId,
                                                 @PathVariable @Min(1) Long requestId) {
        log.info("Canceling request id {} from user id {}.", requestId, userId);
        ParticipationRequestDto canceledRequest = eventService.cancelParticipationRequest(userId, requestId);
        log.info("Request canceled: {}.", canceledRequest);
        return canceledRequest;
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable @Min(1) Long userId,
                                                              @PathVariable @Min(1) Long eventId) {
        log.info("Getting all request to event id {} owned by user {}.", eventId, userId);
        List<ParticipationRequestDto> requests = eventService.getUserEventRequests(userId, eventId);
        log.info("Requests found: {}.", requests);
        return requests;
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable @Min(1) Long userId,
                                                         @PathVariable @Min(1) Long eventId,
                                                         @RequestBody @Validated EventRequestStatusUpdateRequest request) {

        log.info("Updating requests: {}. User id {}. Event id {}.", request, userId, eventId);
        EventRequestStatusUpdateResult result = eventService.updateRequests(userId, eventId, request);
        log.info("Confirmed requests: {}.", result.getConfirmedRequests().stream().map(ParticipationRequestDto::getId)
                .collect(Collectors.toList()));
        log.info("Rejected requests: {}.", result.getRejectedRequests().stream().map(ParticipationRequestDto::getId)
                .collect(Collectors.toList()));
        return result;
    }
}
