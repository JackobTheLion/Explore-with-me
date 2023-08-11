package ru.practicum.explore.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.client.StatsClient;
import ru.practicum.explore.dto.EndpointHitResponseDto;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.exceptions.FailToSaveHitException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class StatService {
    private final StatsClient statsClient;

    public EventFullDto setViewsNumber(EventFullDto event) {
        List<EndpointHitResponseDto> hits = statsClient.getHits(event.getCreatedOn(), LocalDateTime.now(),
                List.of("/events/" + event.getId()), true);
        if (!hits.isEmpty()) {
            event.setViews(hits.get(0).getHits());
        } else {
            event.setViews(0);
        }
        return event;
    }

    public List<EventShortDto> setViewsNumber(List<EventShortDto> events) {
        List<String> uris = new ArrayList<>();
        for (EventShortDto eventShortDto : events) {
            uris.add("/events/" + eventShortDto.getId());
        }
        List<EndpointHitResponseDto> hits = statsClient.getHits(LocalDateTime.now().minusYears(100),
                LocalDateTime.now(), uris, true);
        if (!hits.isEmpty()) {
            Map<Long, Integer> hitMap = mapHits(hits);
            for (EventShortDto event : events) {
                event.setViews(hitMap.getOrDefault(event.getId(), 0));
            }
        } else {
            for (EventShortDto event : events) {
                event.setViews(0);
            }
        }
        return events;
    }

    public void addHit(String uri, String ip) {
        try {
            statsClient.addHit(uri, ip, LocalDateTime.now());
        }  catch (FailToSaveHitException e) {
            log.error("Hit was not saved. Reason: " + e.getMessage());
        }
    }

    private Map<Long, Integer> mapHits(List<EndpointHitResponseDto> hits) {
        Map<Long, Integer> hitMap = new HashMap<>();
        for (EndpointHitResponseDto hit : hits) {
            String hitUri = hit.getUri();
            Long id = Long.valueOf(hitUri.substring(8));
            hitMap.put(id, hit.getHits());
        }
        return hitMap;
    }

}
