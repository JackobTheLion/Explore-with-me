package ru.practicum.explore.dto.search;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PublicSearchCriteria {
    private String text;

    private List<Long> categories;

    private Boolean paid;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private Sort sort;

    private Long searchArea;

    private Integer from;

    private Integer size;
}
