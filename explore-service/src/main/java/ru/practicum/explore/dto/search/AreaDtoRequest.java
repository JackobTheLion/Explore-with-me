package ru.practicum.explore.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDtoRequest {
    @NotNull
    @Size(max = 120)
    private String areaName;

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

    @NotNull
    private Float radius;
}
