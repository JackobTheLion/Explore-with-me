package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.ParticipationRequest;
import ru.practicum.explore.model.ParticipationRequestStatus;

import java.util.List;
import java.util.Map;

public interface EventRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByUserId(Long id);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    Long countAllByEventIdIsAndConfirmed(Long eventId, ParticipationRequestStatus status);

}
