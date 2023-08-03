package ru.practicum.explore.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EndpointHitResponseDto {
    private String app;
    private String uri;
    private Integer hits;
}
