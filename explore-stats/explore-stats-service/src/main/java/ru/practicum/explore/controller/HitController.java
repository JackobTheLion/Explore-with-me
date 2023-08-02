package ru.practicum.explore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.EndpointHitRequestDto;
import ru.practicum.explore.dto.EndpointHitSavedDto;
import ru.practicum.explore.service.StatsService;

@Slf4j
@RestController
@RequestMapping("/hit")
public class HitController {
    private final StatsService statsService;

    @Autowired
    public HitController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitSavedDto addEndpointHit(@RequestBody @Validated EndpointHitRequestDto endpointHitRequestDto) {
        log.info("Adding endpoint hit: {}.", endpointHitRequestDto);
        EndpointHitSavedDto endpointHitSavedDto = statsService.addEndpointHit(endpointHitRequestDto);
        log.info("Endpoint hit added: {}.", endpointHitSavedDto);
        return endpointHitSavedDto;
    }
}
