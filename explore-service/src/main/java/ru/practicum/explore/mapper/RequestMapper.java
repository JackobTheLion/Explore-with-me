package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.participation.ParticipationRequestDto;
import ru.practicum.explore.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ParticipationRequestDto mapToDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .id(participationRequest.getId())
                .requester(participationRequest.getUser().getId())
                .status(participationRequest.getConfirmed())
                .build();
    }

    public static EventRequestStatusUpdateResult mapToRequestUpdateResultDto(List<ParticipationRequest> confirmedRequests,
                                                                             List<ParticipationRequest> rejectedRequests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::mapToDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::mapToDto).collect(Collectors.toList()))
                .build();
    }
}
