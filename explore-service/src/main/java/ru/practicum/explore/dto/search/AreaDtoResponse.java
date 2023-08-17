package ru.practicum.explore.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDtoResponse {
    private Long id;

    private String areaName;

    private Float lat;

    private Float lon;

    private Float radius;
}
