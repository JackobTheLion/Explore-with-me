package ru.practicum.explore.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EndpointHit {
    private Long id;
    private App app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
