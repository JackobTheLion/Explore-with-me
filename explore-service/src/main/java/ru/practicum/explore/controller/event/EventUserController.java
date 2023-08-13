package ru.practicum.explore.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.dto.event.UpdateEventUserRequest;
import ru.practicum.explore.service.EventService;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
@Validated
public class EventUserController {
    private final EventService eventService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable @Min(1) Long userId,
                                  @RequestBody @Validated NewEventDto newEventDto) {
        log.info("Saving event {} by user id {}.", newEventDto, userId);
        EventFullDto savedEvent = eventService.save(newEventDto, userId);
        log.info("Event saved: {}.", savedEvent);
        return savedEvent;
    }

    @GetMapping("/events")
    public List<EventFullDto> getAllUserEvents(@PathVariable @Min(1) Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all events of user id {}. From {}, size {}.", userId, from, size);
        List<EventFullDto> events = eventService.getAllUserEvents(userId, from, size);
        log.info("Events ids found: {}.", events.stream().map(EventFullDto::getId).collect(Collectors.toList()));
        return events;
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable @Min(1) Long userId,
                                     @PathVariable @Min(1) Long eventId) {
        log.info("Getting event id {} of user id {}.", eventId, userId);
        EventFullDto event = eventService.getEventById(userId, eventId);
        log.info("Event found: {}.", event);
        return event;
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Min(1) Long userId,
                                    @PathVariable @Min(1) Long eventId,
                                    @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest) {
        log.info("User {} updating event id {}: {}.", userId, eventId, updateEventUserRequest);
        EventFullDto updatedEvent = eventService.updateEvent(userId, eventId, updateEventUserRequest);
        log.info("Event updated: {}", updatedEvent);
        return updatedEvent;
    }
}
