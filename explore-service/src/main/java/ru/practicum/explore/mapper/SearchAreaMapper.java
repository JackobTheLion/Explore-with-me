package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.search.AreaDtoRequest;
import ru.practicum.explore.dto.search.AreaDtoResponse;
import ru.practicum.explore.model.Area;

public class SearchAreaMapper {
    public static Area mapFromDto(AreaDtoRequest areaDtoRequest) {
        return Area.builder()
                .areaName(areaDtoRequest.getAreaName())
                .lat(areaDtoRequest.getLat())
                .lon(areaDtoRequest.getLon())
                .radius(areaDtoRequest.getRadius())
                .build();
    }

    public static AreaDtoResponse mapToDto(Area area) {
        return AreaDtoResponse.builder()
                .id(area.getId())
                .areaName(area.getAreaName())
                .lat(area.getLat())
                .lon(area.getLon())
                .radius(area.getRadius())
                .build();
    }
}
