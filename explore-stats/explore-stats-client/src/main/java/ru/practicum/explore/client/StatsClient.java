package ru.practicum.explore.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.dto.EndpointHitRequestDto;
import ru.practicum.explore.dto.EndpointHitResponseDto;
import ru.practicum.explore.dto.EndpointHitSavedDto;
import ru.practicum.explore.exceptions.FailToSaveHitException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatsClient {
    private final RestTemplate rest;
    private final String app;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public StatsClient(@Value("{statistics.app.name}") String app, @Value("${statistics.server.url}") String url, RestTemplateBuilder builder) {
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        this.app = app;
    }

    public EndpointHitSavedDto addHit(String uri, String ip, LocalDateTime timestamp) throws FailToSaveHitException {
        EndpointHitRequestDto hit = new EndpointHitRequestDto(app, uri, ip, timestamp);
        log.info("Adding hit to statistics: {}.", hit);
        HttpEntity<EndpointHitRequestDto> requestEntity = new HttpEntity<>(hit);

        try {
            String hitPath = "/hit";
            EndpointHitSavedDto savedHit = rest.postForObject(hitPath, requestEntity, EndpointHitSavedDto.class);
            log.info("Hit saved: {}.", savedHit);
            return savedHit;
        } catch (RuntimeException e) {
            log.info("Hit not saved: {}", e.getMessage());
            throw new FailToSaveHitException(String.format("Hit not saved: %s", e.getMessage()));
        }
    }

    public List<EndpointHitResponseDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Getting hits from {} to {} for uris: {}. Unique: {}", start, end, uris, unique);
        Map<String, Object> parameters = mapParameters(start, end, uris, unique);
        try {
            //HttpEntity<List<EndpointHitResponseDto>> httpEntity = new HttpEntity<>(null);
            String getStatPath = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
            ResponseEntity<List<EndpointHitResponseDto>> response = rest.exchange(getStatPath, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {
                    }, parameters);
            List<EndpointHitResponseDto> hits = response.getBody();
            log.info("Hits received: {}", hits);
            return hits;
        } catch (HttpStatusCodeException e) {
            log.info("Could not receive hits: {}", e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> mapParameters(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris != null) {
            return Map.of(
                    "start", dateTimeFormatter.format(start),
                    "end", dateTimeFormatter.format(end),
                    "uris", uris.toArray(),
                    "unique", unique
            );
        } else {
            return Map.of(
                    "start", dateTimeFormatter.format(start),
                    "end", dateTimeFormatter.format(end),
                    "unique", unique
            );
        }
    }
}
