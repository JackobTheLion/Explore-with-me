package ru.practicum.explore.dto.area;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.model.AreaStatus;

import javax.validation.constraints.*;

import static ru.practicum.explore.validation.ValidationGroups.Create;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDtoRequest {
    @NotBlank(groups = Create.class)
    @Size(max = 120)
    private String areaName;

    @NotNull(groups = Create.class)
    @Min(-90)
    @Max(90)
    private Float lat;

    @NotNull(groups = Create.class)
    @Min(-180)
    @Max(180)
    private Float lon;

    @NotNull(groups = Create.class)
    @Positive
    private Float radius;

    private AreaStatus areaStatus = AreaStatus.OPEN;
}
