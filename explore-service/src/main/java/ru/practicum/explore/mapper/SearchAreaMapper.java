package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.area.AreaDtoRequest;
import ru.practicum.explore.dto.area.AreaDtoResponseAdmin;
import ru.practicum.explore.dto.area.AreaDtoResponsePublic;
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

    public static AreaDtoResponseAdmin mapToAdminDto(Area area) {
        return AreaDtoResponseAdmin.builder()
                .id(area.getId())
                .areaName(area.getAreaName())
                .lat(area.getLat())
                .lon(area.getLon())
                .radius(area.getRadius())
                .areaStatus(area.getAreaStatus())
                .build();
    }

    public static AreaDtoResponsePublic mapToPublicDto(Area area) {
        return AreaDtoResponsePublic.builder()
                .id(area.getId())
                .areaName(area.getAreaName())
                .lat(area.getLat())
                .lon(area.getLon())
                .radius(area.getRadius())
                .build();
    }
}
