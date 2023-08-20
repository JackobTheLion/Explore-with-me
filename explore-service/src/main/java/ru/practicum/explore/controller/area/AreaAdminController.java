package ru.practicum.explore.controller.area;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.area.AreaDtoRequest;
import ru.practicum.explore.dto.area.AreaDtoResponseAdmin;
import ru.practicum.explore.service.AreaService;
import ru.practicum.explore.validation.ValidationGroups;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/areas")
@Validated
public class AreaAdminController {

    private final AreaService areaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AreaDtoResponseAdmin saveArea(@RequestBody @Validated(ValidationGroups.Create.class) AreaDtoRequest areaDtoRequest) {
        log.info("Adding area: {}", areaService);
        AreaDtoResponseAdmin savedArea = areaService.saveArea(areaDtoRequest);
        log.info("Area saved: {}.", savedArea);
        return savedArea;
    }

    @PatchMapping("/{areaId}")
    public AreaDtoResponseAdmin patchArea(@PathVariable @Min(1) Long areaId,
                                          @RequestBody @Validated AreaDtoRequest areaDtoRequest) {
        log.info("Updating area id {} as: {}", areaId, areaDtoRequest);
        AreaDtoResponseAdmin updatedArea = areaService.patchArea(areaId, areaDtoRequest);
        log.info("Area updated: {}.", updatedArea);
        return updatedArea;
    }

    @GetMapping
    public List<AreaDtoResponseAdmin> getAllAreas(@RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all search areas from {} page size {}", from, size);
        List<AreaDtoResponseAdmin> foundAreas = areaService.getAllAreasAdmin(from, size);
        log.info("Areas found: {}", foundAreas);
        return foundAreas;
    }

    @GetMapping("{areaId}")
    public AreaDtoResponseAdmin getArea(@PathVariable @Min(1) Long areaId) {
        log.info("Looking for area id: {}", areaId);
        AreaDtoResponseAdmin area = areaService.getAreaAdmin(areaId);
        log.info("Area found: {}", area);
        return area;
    }
}
