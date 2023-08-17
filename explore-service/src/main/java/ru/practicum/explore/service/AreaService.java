package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.search.AreaDtoRequest;
import ru.practicum.explore.dto.search.AreaDtoResponse;
import ru.practicum.explore.exception.exceptions.AreaNotFoundException;
import ru.practicum.explore.mapper.SearchAreaMapper;
import ru.practicum.explore.model.Area;
import ru.practicum.explore.repository.AreaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaDtoResponse saveArea(AreaDtoRequest areaDtoRequest) {
        log.info("Adding search area request: {}", areaDtoRequest);
        Area area = SearchAreaMapper.mapFromDto(areaDtoRequest);
        Area savedArea = areaRepository.save(area);
        log.info("Area saved: {}", savedArea);
        return SearchAreaMapper.mapToDto(savedArea);
    }

    public List<AreaDtoResponse> getAllAreas(Integer from, Integer size) {
        log.info("Getting all areas.");
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Area> areas = areaRepository.findAllBy(page).getContent();
        log.info("Areas found: {}", areas);
        return areas.stream().map(SearchAreaMapper::mapToDto).collect(Collectors.toList());
    }

    public AreaDtoResponse getArea(Long areaId) {
        log.info("Looking for area id: {}", areaId);
        Area area = areaRepository.findById(areaId).orElseThrow(() -> {
            log.info("Area id {} not found.", areaId);
            return new AreaNotFoundException(String.format("Area id %s not found.", areaId));
        });
        log.info("Area found: {}.", area);
        return SearchAreaMapper.mapToDto(area);
    }
}
