package ru.practicum.explore.dto.event;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEvent {
    private StateActionAdmin stateAction;
}
