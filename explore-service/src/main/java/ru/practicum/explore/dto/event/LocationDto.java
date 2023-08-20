package ru.practicum.explore.dto.event;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class LocationDto {
    @NotNull
    @Min(-90)
    @Max(90)
    private Float lat;

    @NotNull
    @Min(-180)
    @Max(180)
    private Float lon;
}
