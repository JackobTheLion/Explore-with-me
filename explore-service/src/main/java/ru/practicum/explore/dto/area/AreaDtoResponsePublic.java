package ru.practicum.explore.dto.area;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDtoResponsePublic {

    private Long id;

    private String areaName;

    private Float lat;

    private Float lon;

    private Float radius;
}
