package ru.practicum.explore.dto.event;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class LocationDto {
    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
