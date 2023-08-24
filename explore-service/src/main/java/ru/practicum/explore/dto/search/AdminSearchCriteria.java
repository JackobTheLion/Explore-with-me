package ru.practicum.explore.dto.search;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explore.mapper.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminSearchCriteria {
    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private Long searchArea;

    private Integer from;

    private Integer size;
}
