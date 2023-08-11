package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.dto.event.UpdateEventUserRequest;
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
@RequestMapping("/users")
@Validated
public class UserController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable @Min(1) Long userId,
                                  @RequestBody @Validated NewEventDto newEventDto) {
        log.info("Saving event {} by user id {}.", newEventDto, userId);
        EventFullDto savedEvent = eventService.save(newEventDto, userId);
        log.info("Event saved: {}.", savedEvent);
        return savedEvent;
    }

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getAllUserEvents(@PathVariable @Min(1) Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all events of user id {}. From {}, size {}.", userId, from, size);
        List<EventFullDto> events = eventService.getAllUserEvents(userId, from, size);
        log.info("Events ids found: {}.", events.stream().map(EventFullDto::getId).collect(Collectors.toList()));
        return events;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable @Min(1) Long userId,
                                     @PathVariable @Min(1) Long eventId) {
        log.info("Getting event id {} of user id {}.", eventId, userId);
        EventFullDto event = eventService.getEventById(userId, eventId);
        log.info("Event found: {}.", event);
        return event;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Min(1) Long userId,
                                    @PathVariable @Min(1) Long eventId,
                                    @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest) {
        log.info("User {} updating event id {}: {}.", userId, eventId, updateEventUserRequest);
        EventFullDto updatedEvent = eventService.updateEvent(userId, eventId, updateEventUserRequest);
        log.info("Event updated: {}", updatedEvent);
        return updatedEvent;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable @Min(1) Long userId,
                                                           @RequestParam @Min(1) Long eventId) {
        log.info("Adding participation request from user id {} to event id {}.", userId, eventId);
        ParticipationRequestDto savedRequest = eventService.addRequest(userId, eventId);
        log.info("Request added: {}.", savedRequest);
        return savedRequest;
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable @Min(1) Long userId) {
        log.info("Looking for requests of user id {}.", userId);
        List<ParticipationRequestDto> userRequests = eventService.getUserRequests(userId);
        log.info("Requests found: {}.", userRequests);
        return userRequests;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Min(1) Long userId,
                                                 @PathVariable @Min(1) Long requestId) {
        log.info("Canceling request id {} from user id {}.", requestId, userId);
        ParticipationRequestDto canceledRequest = eventService.cancelParticipationRequest(userId, requestId);
        log.info("Request canceled: {}.", canceledRequest);
        return canceledRequest;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable @Min(1) Long userId,
                                                              @PathVariable @Min(1) Long eventId) {
        log.info("Getting all request to event id {} owned by user {}.", eventId, userId);
        List<ParticipationRequestDto> requests = eventService.getUserEventRequests(userId, eventId);
        log.info("Requests found: {}.", requests);
        return requests;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
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
