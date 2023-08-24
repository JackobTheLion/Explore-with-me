package ru.practicum.explore.dto.area;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.model.AreaStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDtoResponseAdmin {

    private Long id;

    private String areaName;

    private Float lat;

    private Float lon;

    private Float radius;

    private AreaStatus areaStatus;

}
