package ru.practicum.explore.dto.area;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.model.AreaStatus;
import ru.practicum.explore.validation.ValidationGroups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ru.practicum.explore.validation.ValidationGroups.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDtoRequest {
    @NotNull(groups = Create.class)
    @Size(max = 120)
    private String areaName;

    @NotNull(groups = Create.class)
    private Float lat;

    @NotNull(groups = Create.class)
    private Float lon;

    @NotNull(groups = Create.class)
    private Float radius;

    private AreaStatus areaStatus;
}
