package ru.practicum.explore.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.UpdateEventAdminRequest;
import ru.practicum.explore.dto.search.AdminSearchCriteria;
import ru.practicum.explore.mapper.EventState;
import ru.practicum.explore.service.EventService;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class EventAdminController {

    private final EventService eventService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping()
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false) Long searchArea,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                log.info("Start date {} is after end date {}.", start, end);
                throw new ValidationException(String.format("Start date %s is after end date %s.", start, end));
            }
        }

        AdminSearchCriteria adminSearchCriteria = AdminSearchCriteria.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .searchArea(searchArea)
                .from(from)
                .size(size)
                .rangeStart(start)
                .rangeEnd(end)
                .build();

        log.info("Searching events based on criteria: {}.", adminSearchCriteria);
        List<EventFullDto> events = eventService.adminGetEvents(adminSearchCriteria);
        log.info("Events found: {}", events);
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable @Min(1) Long eventId,
                                   @RequestBody @Validated UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Admin updating event id {}: {}", eventId, updateEventAdminRequest);
        EventFullDto eventFullDto = eventService.adminUpdateEvent(eventId, updateEventAdminRequest);
        log.info("Event updated: {}.", eventFullDto);
        return eventFullDto;
    }
}