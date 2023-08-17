package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.event.*;
import ru.practicum.explore.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.participation.ParticipationRequestDto;
import ru.practicum.explore.dto.search.AdminSearchCriteria;
import ru.practicum.explore.dto.search.PublicSearchCriteria;
import ru.practicum.explore.dto.search.Sort;
import ru.practicum.explore.exception.exceptions.*;
import ru.practicum.explore.mapper.EventMapper;
import ru.practicum.explore.mapper.EventState;
import ru.practicum.explore.mapper.RequestMapper;
import ru.practicum.explore.model.*;
import ru.practicum.explore.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.explore.mapper.EventMapper.mapToFullDto;
import static ru.practicum.explore.model.ParticipationRequestStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final EventRequestRepository eventRequestRepository;

    private final CustomEventRepository customEventRepository;

    private final AreaRepository areaRepository;

    private final StatService statService;

    public EventFullDto save(NewEventDto newEventDto, Long userId) {
        log.info("Saving event {} by user id {}.", newEventDto, userId);
        User initiator = getUser(userId);
        Category category = getCategory(newEventDto.getCategory());
        Location savedLocation = getLocation(newEventDto.getLocation());
        Event eventToSave = EventMapper.mapFromNewDto(newEventDto, initiator, category, savedLocation);
        eventToSave.setCreatedOn(LocalDateTime.now());
        Event savedEvent = eventRepository.save(eventToSave);
        log.info("Event saved: {}.", savedEvent);
        return mapToFullDto(savedEvent);
    }

    public List<EventFullDto> getAllUserEvents(Long userId, Integer from, Integer size) {
        log.info("Getting all events of user id {}. From {}, size {}.", userId, from, size);
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, page).getContent();
        List<EventFullDto> eventsDto = events.stream()
                .map(EventMapper::mapToFullDto)
                .map(statService::setViewsNumber)
                .map(this::setConfirmedRequests)
                .collect(Collectors.toList());
        log.info("Events ids found: {}.", eventsDto.stream().map(EventFullDto::getId).collect(Collectors.toList()));
        return eventsDto;
    }

    public EventFullDto getEventById(Long userId, Long eventId) {
        log.info("Looking for event id {} by user id {}", eventId, userId);
        Event savedEvent = getEventByUser(userId, eventId);
        log.info("Event found: {}.", savedEvent);
        EventFullDto eventFullDto = mapToFullDto(savedEvent);
        statService.setViewsNumber(eventFullDto);
        setConfirmedRequests(eventFullDto);
        return eventFullDto;
    }

    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventRequest) {
        log.info("User {} updating event id {}: {}.", userId, eventId, updateEventRequest);

        Event eventToUpdate = getEventByUser(userId, eventId);

        if (eventToUpdate.getState() == EventState.PUBLISHED) {
            log.info("Event status is published. Cannot update.");
            throw new UpdateEventImpossibleException("Event status is published. Cannot update.");
        }
        updateEventFields(eventToUpdate, updateEventRequest);

        if (updateEventRequest.getStateAction() != null) {
            switch (updateEventRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    eventToUpdate.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventToUpdate.setState(EventState.PENDING);
            }
        }

        Event updatedEvent = eventRepository.save(eventToUpdate);
        log.info("Event updated: {}.", updatedEvent);
        EventFullDto updatedEventDto = mapToFullDto(updatedEvent);
        statService.setViewsNumber(updatedEventDto);
        setConfirmedRequests(updatedEventDto);
        return updatedEventDto;
    }

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info("Adding request from {} to event {}.", userId, eventId);
        User requester = getUser(userId);
        Event event = getEvent(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            log.error("Cannot register for unpublished event.");
            throw new ParticipationRequestNotCreatedException("Cannot register for unpublished event.");
        }

        if (event.getInitiator().equals(requester)) {
            log.error("Cannot register for own event.");
            throw new ParticipationRequestNotCreatedException("Cannot register for own event.");
        }

        List<ParticipationRequest> requests = eventRequestRepository.findAllByEventId(eventId);

        List<ParticipationRequest> confirmedRequests = requests.stream()
                .filter((pr) -> pr.getConfirmed().equals(CONFIRMED))
                .collect(Collectors.toList());

        if (event.getParticipantsLimit() > 0 && Long.valueOf(confirmedRequests.size()).equals(event.getParticipantsLimit())) {
            log.error("Maximum participants already registered.");
            throw new ParticipationRequestNotCreatedException("Maximum participants already registered.");
        }

        Optional<ParticipationRequest> first = requests.stream()
                .filter((r) -> r.getUser().equals(requester))
                .findFirst();

        if (first.isEmpty()) {
            ParticipationRequest participationRequest = ParticipationRequest.builder()
                    .user(requester)
                    .event(event)
                    .created(LocalDateTime.now())
                    .build();

            if (!event.getRequestModeration() || event.getParticipantsLimit() == 0) {
                participationRequest.setConfirmed(CONFIRMED);
            } else {
                participationRequest.setConfirmed(PENDING);
            }

            ParticipationRequest savedRequest = eventRequestRepository.save(participationRequest);
            log.info("Request saved: {}.", savedRequest);
            return RequestMapper.mapToDto(savedRequest);
        } else {
            log.info("Request from user id {} to event id {} already exists.", userId, eventId);
            throw new ParticipationRequestNotCreatedException(String
                    .format("Request from user id %s to event id %s already exists.", userId, eventId));
        }
    }

    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        log.info("Canceling request id {} from user id {}.", requestId, userId);
        ParticipationRequest request = getRequest(requestId);
        if (!request.getUser().getId().equals(userId)) {
            log.info("Request id {} does not belong to user id {}.", requestId, userId);
            throw new RequestNotFoundException(String.format("Request id %s not found.", requestId));
        }
        request.setConfirmed(CANCELED);
        log.info("Request cancelled: {}.", request);
        return RequestMapper.mapToDto(eventRequestRepository.save(request));
    }

    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        log.info("Getting all request to event id {} owned by user {}.", eventId, userId);
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.info("Event id {} does not belong to user id {}.", event, userId);
            throw new AccessDeniedException("You have no rights to this event.");
        }
        List<ParticipationRequest> allByEventId = eventRequestRepository.findAllByEventId(eventId);
        log.info("Requests found: {}.", allByEventId);
        return allByEventId.stream().map(RequestMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateRequest) {
        log.info("Updating requests: {}. User id {}. Event id {}.", updateRequest, userId, eventId);
        getUser(userId);
        Event event = getEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            log.info("Event id {} does not belong to user id {}.", event, userId);
            throw new AccessDeniedException("You have no rights to this event.");
        }

        List<ParticipationRequest> requestsToUpdate = eventRequestRepository.findAllByIdIn(updateRequest.getRequestIds());

        Long confirmedRequests = eventRequestRepository.countAllByEventIdIsAndConfirmed(eventId, CONFIRMED);
        if (!(event.getParticipantsLimit() == 0) && event.getParticipantsLimit().equals(confirmedRequests)) {
            log.error("Participants limit for event id {} already reached", eventId);
            throw new RequestCannotBeUpdatedException(String
                    .format("Participants limit for event id %s already reached", eventId));
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest request : requestsToUpdate) {
            if (!request.getEvent().getId().equals(eventId)) {
                log.info("Request id {} does not belong to event id {}", request.getId(), eventId);
                rejected.add(request);
                continue;
            }
            switch (updateRequest.getStatus()) {
                case CONFIRMED:
                    if (confirmedRequests < event.getParticipantsLimit()) {
                        request.setConfirmed(CONFIRMED);
                        confirmedRequests++;
                        confirmed.add(request);
                        log.info("Request {} status updated.", request);
                    } else {
                        request.setConfirmed(REJECTED);
                        rejected.add(request);
                        log.info("Maximum participants reached. Request {} rejected", request);
                    }
                    break;
                case REJECTED:
                    request.setConfirmed(REJECTED);
                    rejected.add(request);
                    log.info("Request {} rejected by user id {}.", request, userId);
            }
        }
        return RequestMapper.mapToRequestUpdateResultDto(confirmed, rejected);
    }

    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Admin updating event id {}: {}", eventId, updateEventAdminRequest);
        Event eventToUpdate = getEvent(eventId);

        switch (eventToUpdate.getState()) {
            case PUBLISHED:
                if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                    log.error("Event already published");
                    throw new UpdateEventImpossibleException("Event already published");
                } else if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                    log.error("Cannot cancel published event");
                    throw new UpdateEventImpossibleException("Cannot cancel published event");
                }
                break;
            case CANCELED:
                if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                    log.error("Cannot publish rejected event");
                    throw new UpdateEventImpossibleException("Cannot publish rejected event");
                } else if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                    log.error("Event already rejected");
                    throw new UpdateEventImpossibleException("Event already rejected");
                }
                break;
            case PENDING:
                if (updateEventAdminRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                    eventToUpdate.setState(EventState.PUBLISHED);
                } else if (updateEventAdminRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                    eventToUpdate.setState(EventState.CANCELED);
                }
                eventToUpdate.setPublishedOn(LocalDateTime.now());
                break;
        }

        updateEventFields(eventToUpdate, updateEventAdminRequest);

        Event updatedEvent = eventRepository.save(eventToUpdate);
        log.info("Event updated by admin: {}.", updatedEvent);
        EventFullDto updatedEventDto = mapToFullDto(updatedEvent);
        statService.setViewsNumber(updatedEventDto);
        setConfirmedRequests(updatedEventDto);
        return updatedEventDto;
    }

    public List<EventShortDto> publicGetEvents(PublicSearchCriteria publicSearchCriteria, String ip, String uri) {
        log.info("Searching events based on criteria: {}.", publicSearchCriteria);
        List<Event> events = customEventRepository.findEventsPublic(publicSearchCriteria);
        log.info("Events found: {}.", events);

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<EventShortDto> shortDto = events.stream()
                .map(EventMapper::mapToShortDto)
                .collect(Collectors.toList());
        statService.setViewsNumber(shortDto);
        setConfirmedRequests(shortDto);
        for (EventShortDto event : shortDto) {
            statService.addHit("/events/" + event.getId(), ip);
        }
        if (publicSearchCriteria.getSort() == Sort.VIEWS) {
            return shortDto.stream().sorted(Comparator.comparingInt(EventShortDto::getViews)).collect(Collectors.toList());
        } else {
            return shortDto.stream().sorted(Comparator.comparing(EventShortDto::getEventDate)).collect(Collectors.toList());
        }
    }

    public List<EventFullDto> adminGetEvents(AdminSearchCriteria adminSearchCriteria) {
        log.info("Searching events based on criteria: {}.", adminSearchCriteria);
        List<Event> events = customEventRepository.findEventsAdmin(adminSearchCriteria);
        log.info("Events found: {}.", events);
        return events.stream()
                .map(EventMapper::mapToFullDto)
                .map(statService::setViewsNumber)
                .map(this::setConfirmedRequests)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventPublic(Long eventId, String ip, String uri) {
        log.info("Looking for event id {}.", eventId);
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() -> {
            log.error("Event id {} not found.", eventId);
            return new EventNotFoundException(String.format("Event id %s not found.", eventId));
        });

        log.info("Event found: {}", event);
        EventFullDto eventFullDto = statService.setViewsNumber(mapToFullDto(event));
        statService.addHit(uri, ip);
        setConfirmedRequests(eventFullDto);
        return eventFullDto;
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Looking for requests of user id {}.", userId);
        List<ParticipationRequest> userRequests = eventRequestRepository.findAllByUserId(userId);
        log.info("Requests found: {}.", userRequests);
        return userRequests.stream().map(RequestMapper::mapToDto).collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id {} not found.", userId);
            return new UserNotFoundException(String.format("User id %s not found.", userId));
        });
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event id {} not found.", eventId);
            return new EventNotFoundException(String.format("Event id %s not found.", eventId));
        });
    }

    private Event getEventByUser(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("Event id {} by user id {} not found.", eventId, userId);
            return new EventNotFoundException(String.format("Event id %s by user id %s not found.", eventId, userId));
        });
    }

    private Location getLocation(LocationDto locationDto) {
        log.info("Checking location: {}.", locationDto);
        Location savedLocation = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon()).orElseGet(() -> {
            log.warn("Location {} not found. Adding location.", locationDto);
            return locationRepository.save(new Location(locationDto.getLat(), locationDto.getLon()));
        });
        log.info("Saved location: {}.", savedLocation);
        return savedLocation;
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category id {} not found.", categoryId);
            return new CategoryNotFoundException(String.format("Category id %s not found.", categoryId));
        });
    }

    private ParticipationRequest getRequest(Long requestId) {
        return eventRequestRepository.findById(requestId).orElseThrow(() -> {
            log.info("Request id {} not found.", requestId);
            return new RequestNotFoundException(String.format("Request id %s not found.", requestId));
        });
    }

    private void updateEventFields(Event eventToUpdate, UpdateEvent updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updateEventRequest.getAnnotation());
            log.debug("Annotation updated to: {}", updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null
                && !updateEventRequest.getCategory().equals(eventToUpdate.getCategory().getId())) {
            Category category = getCategory(updateEventRequest.getCategory());
            eventToUpdate.setCategory(category);
            log.debug("Category updated to: {}", category);
        }

        if (updateEventRequest.getDescription() != null) {
            eventToUpdate.setDescription(updateEventRequest.getDescription());
            log.debug("Description updated to: {}", updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            eventToUpdate.setEventDate(updateEventRequest.getEventDate());
            log.debug("Event date updated to: {}", updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getLocation() != null) {
            Location newLocation = getLocation(updateEventRequest.getLocation());
            eventToUpdate.setLocation(newLocation);
            log.debug("Event location updated to: {}", updateEventRequest.getLocation());
        }

        if (updateEventRequest.getPaid() != null) {
            eventToUpdate.setPaid(updateEventRequest.getPaid());
            log.debug("Paid updated to: {}", updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            eventToUpdate.setParticipantsLimit(updateEventRequest.getParticipantLimit());
            log.debug("Participant limit updated to: {}", updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(updateEventRequest.getRequestModeration());
            log.debug("Request moderation updated to: {}", updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            eventToUpdate.setTitle(updateEventRequest.getTitle());
            log.debug("Title updated to: {}", updateEventRequest.getTitle());
        }
    }

    private EventFullDto setConfirmedRequests(EventFullDto event) {
        event.setConfirmedRequests(eventRequestRepository.countAllByEventIdIsAndConfirmed(event.getId(), CONFIRMED));
        return event;
    }

    private void setConfirmedRequests(List<EventShortDto> events) {
        for (EventShortDto event : events) {
            event.setConfirmedRequests(eventRequestRepository.countAllByEventIdIsAndConfirmed(event.getId(), CONFIRMED));
        }
    }

}
