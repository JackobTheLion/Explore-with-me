package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.EndpointHitRequestDto;
import ru.practicum.explore.dto.EndpointHitSavedDto;
import ru.practicum.explore.model.App;
import ru.practicum.explore.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit mapFromDto(EndpointHitRequestDto endpointHitRequestDto) {
        return EndpointHit.builder()
                .app(new App(endpointHitRequestDto.getApp()))
                .uri(endpointHitRequestDto.getUri())
                .ip(endpointHitRequestDto.getIp())
                .timestamp(endpointHitRequestDto.getTimestamp())
                .build();
    }

    public static EndpointHitSavedDto mapToDto(EndpointHit endpointHit) {
        return EndpointHitSavedDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp().getAppName())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}
