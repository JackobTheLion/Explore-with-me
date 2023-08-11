package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.Location;
import ru.practicum.explore.model.User;

import static ru.practicum.explore.mapper.EventState.PENDING;

public class EventMapper {
    public static Event mapFromNewDto(NewEventDto newEventDto, User initiator, Category category, Location location) {
        Event event = Event.builder()
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(location)
                .participantsLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .initiator(initiator)
                .category(category)
                .state(PENDING)
                .annotation(newEventDto.getAnnotation())
                .build();

        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        } else event.setPaid(false);

        return event;
    }

    public static EventFullDto mapToFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.mapToShortDto(event.getInitiator()))
                .location(LocationMapper.mapToDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantsLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();

        if (event.getParticipationRequests() != null && !event.getParticipationRequests().isEmpty()) {
            eventFullDto.setConfirmedRequests(event.getParticipationRequests().stream()
                    .filter(participationRequest -> false)
                    .count());
        } else eventFullDto.setConfirmedRequests(0L);
        return eventFullDto;
    }

    public static EventShortDto mapToShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.mapToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }
}
