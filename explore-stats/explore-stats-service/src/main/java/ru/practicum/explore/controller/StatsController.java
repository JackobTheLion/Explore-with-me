package ru.practicum.explore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.dto.EndpointHitResponseDto;
import ru.practicum.explore.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsService statsService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public List<EndpointHitResponseDto> getStats(@RequestParam String start,
                                                 @RequestParam String end,
                                                 @RequestParam(required = false) List<String> uris,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        try {
            LocalDateTime startDate = LocalDateTime.parse(start, dateTimeFormatter);
            LocalDateTime endDate = LocalDateTime.parse(end, dateTimeFormatter);

            if (startDate.isAfter(endDate)) {
                log.warn("Start date {} must be before end date {}.", startDate, endDate);
                throw new IllegalArgumentException(String.format("Start date %s must be before end date %s.", startDate, endDate));
            }
            log.info("Getting hits from {} to {} for uris: {}. Unique: {}", startDate, endDate, uris, unique);
            List<EndpointHitResponseDto> hits = statsService.getHits(startDate, endDate, uris, unique);
            log.info("Hits: {}", hits);
            return hits;
        } catch (DateTimeParseException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }
}
