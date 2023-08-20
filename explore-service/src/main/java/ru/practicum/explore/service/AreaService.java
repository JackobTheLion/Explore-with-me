package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.area.AreaDtoRequest;
import ru.practicum.explore.dto.area.AreaDtoResponseAdmin;
import ru.practicum.explore.dto.area.AreaDtoResponsePublic;
import ru.practicum.explore.exception.exceptions.AreaNotFoundException;
import ru.practicum.explore.mapper.SearchAreaMapper;
import ru.practicum.explore.model.Area;
import ru.practicum.explore.model.AreaStatus;
import ru.practicum.explore.repository.AreaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaDtoResponseAdmin saveArea(AreaDtoRequest areaDtoRequest) {
        log.info("Adding search area request: {}", areaDtoRequest);
        Area area = SearchAreaMapper.mapFromDto(areaDtoRequest);
        Area savedArea = areaRepository.save(area);
        log.info("Area saved: {}", savedArea);
        return SearchAreaMapper.mapToAdminDto(savedArea);
    }

    public List<AreaDtoResponseAdmin> getAllAreasAdmin(Integer from, Integer size) {
        log.info("Getting all areas.");
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Area> areas = areaRepository.findAllByOrderById(page).getContent();
        log.info("Areas found: {}", areas);
        return areas.stream().map(SearchAreaMapper::mapToAdminDto).collect(Collectors.toList());
    }

    public AreaDtoResponseAdmin getAreaAdmin(Long areaId) {
        log.info("Looking for area id: {}", areaId);
        Area area = getArea(areaId);
        log.info("Area found: {}.", area);
        return SearchAreaMapper.mapToAdminDto(area);
    }

    public AreaDtoResponseAdmin patchArea(Long areaId, AreaDtoRequest areaDtoRequest) {
        log.info("Updating area id {} with {}.", areaId, areaDtoRequest);
        Area areaToUpdate = getArea(areaId);

        if (areaDtoRequest.getAreaName() != null) {
            areaToUpdate.setAreaName(areaDtoRequest.getAreaName());
        }

        if (areaDtoRequest.getLat() != null) {
            areaToUpdate.setLat(areaDtoRequest.getLat());
        }

        if (areaDtoRequest.getLon() != null) {
            areaToUpdate.setLon(areaDtoRequest.getLon());
        }

        if (areaDtoRequest.getRadius() != null) {
            areaToUpdate.setRadius(areaDtoRequest.getRadius());
        }

        if (areaDtoRequest.getAreaStatus() != null) {
            areaToUpdate.setAreaStatus(areaDtoRequest.getAreaStatus());
        }

        Area updatedArea = areaRepository.save(areaToUpdate);
        log.info("Area updated: {}.", updatedArea);
        return SearchAreaMapper.mapToAdminDto(updatedArea);
    }

    public List<AreaDtoResponsePublic> getAllAreasPublic(Integer from, Integer size) {
        log.info("Getting all areas.");
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Area> areas = areaRepository.findAllByAreaStatusOrderById(page, AreaStatus.OPEN).getContent();
        log.info("Areas found: {}", areas);
        return areas.stream().map(SearchAreaMapper::mapToPublicDto).collect(Collectors.toList());
    }

    public AreaDtoResponsePublic getAreaPublic(Long areaId) {
        log.info("Looking for area id: {}", areaId);
        Area area = areaRepository.findByIdAndAreaStatus(areaId, AreaStatus.OPEN).orElseThrow(() -> {
            log.info("Area id {} not found.", areaId);
            return new AreaNotFoundException(String.format("Area id %s not found.", areaId));
        });
        log.info("Area found: {}.", area);
        return SearchAreaMapper.mapToPublicDto(area);
    }

    private Area getArea(Long areaId) {
        return areaRepository.findById(areaId).orElseThrow(() -> {
            log.info("Area id {} not found.", areaId);
            return new AreaNotFoundException(String.format("Area id %s not found.", areaId));
        });
    }
}
