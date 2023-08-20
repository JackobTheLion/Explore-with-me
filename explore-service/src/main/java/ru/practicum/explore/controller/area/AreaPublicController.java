package ru.practicum.explore.controller.area;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.area.AreaDtoResponsePublic;
import ru.practicum.explore.service.AreaService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/areas")
@Validated
public class AreaPublicController {

    private final AreaService areaService;

    @GetMapping
    public List<AreaDtoResponsePublic> getAreas(@RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {

        log.info("Getting all search areas from {} page size {}", from, size);
        List<AreaDtoResponsePublic> foundAreas = areaService.getAllAreasPublic(from, size);
        log.info("Areas found: {}", foundAreas);
        return foundAreas;
    }

    @GetMapping("{areaId}")
    public AreaDtoResponsePublic getArea(@PathVariable @Min(1) Long areaId) {
        log.info("Looking for area id: {}", areaId);
        AreaDtoResponsePublic area = areaService.getAreaPublic(areaId);
        log.info("Area found: {}", area);
        return area;
    }
}
