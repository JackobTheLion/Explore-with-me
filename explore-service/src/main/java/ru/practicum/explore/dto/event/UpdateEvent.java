package ru.practicum.explore.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.validation.StartDateValidation;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public abstract class UpdateEvent {
    @Size(min = 20, max = 2000)
    protected String annotation;

    protected Long category;

    @Size(min = 20, max = 7000)
    protected String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StartDateValidation
    protected LocalDateTime eventDate;

    protected LocationDto location;

    protected Boolean paid;

    protected Long participantLimit;

    protected Boolean requestModeration;

    @Size(min = 3, max = 120)
    protected String title;
}
