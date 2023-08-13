package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.event.LocationDto;
import ru.practicum.explore.model.Location;

public class LocationMapper {
    public static LocationDto mapToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
