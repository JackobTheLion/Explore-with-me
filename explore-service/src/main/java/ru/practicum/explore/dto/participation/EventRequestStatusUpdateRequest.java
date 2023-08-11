package ru.practicum.explore.dto.participation;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explore.model.ParticipationRequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private ParticipationRequestStatus status;
}
