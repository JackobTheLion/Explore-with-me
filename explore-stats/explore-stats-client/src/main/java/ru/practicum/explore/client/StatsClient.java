package ru.practicum.explore.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.dto.EndpointHitRequestDto;
import ru.practicum.explore.dto.EndpointHitSavedDto;
import ru.practicum.explore.exceptions.FailToSaveHitException;

import java.time.LocalDateTime;

@Slf4j
@Service
public class StatsClient {
    private final RestTemplate rest;
    private final static String PATH = "/hit";

    @Autowired
    public StatsClient(@Value("${statistics.server.url}") String url, RestTemplateBuilder builder) {
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public EndpointHitSavedDto addHit(String app, String uri, String ip, LocalDateTime timestamp) throws FailToSaveHitException {
        EndpointHitRequestDto hit = new EndpointHitRequestDto(app, uri, ip, timestamp);
        log.info("Adding hit to statistics: {}.", hit);
        HttpEntity<EndpointHitRequestDto> requestEntity = new HttpEntity<>(hit);

        ResponseEntity<EndpointHitSavedDto> statsServerResponse;
        try {
            statsServerResponse = rest.exchange(PATH, HttpMethod.POST, requestEntity, EndpointHitSavedDto.class);
            EndpointHitSavedDto savedHit = statsServerResponse.getBody();
            log.info("Hit saved: {}.", savedHit);
            return savedHit;
        } catch (RuntimeException e) {
            log.info("Hit not saved: {}", e.getMessage());
            throw new FailToSaveHitException(String.format("Hit not saved: %s", e.getMessage()));
        }
    }
}
