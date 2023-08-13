package ru.practicum.explore.dto.event;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest extends UpdateEvent {
    private StateActionUser stateAction;
}
