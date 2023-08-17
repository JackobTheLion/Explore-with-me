package ru.practicum.explore.controller.searcharea;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.dto.search.AreaDtoRequest;
import ru.practicum.explore.dto.search.AreaDtoResponse;
import ru.practicum.explore.service.AreaService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/areas")
@Validated
public class AdminSearchAreaController {

    private final AreaService areaService;

    @PostMapping
    public AreaDtoResponse saveArea(@RequestBody @Validated AreaDtoRequest areaDtoRequest) {
        log.info("Adding area: {}", areaService);
        AreaDtoResponse savedArea = areaService.saveArea(areaDtoRequest);
        log.info("Area saved: {}.", savedArea);
        return savedArea;
    }
}
