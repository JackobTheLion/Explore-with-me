package ru.practicum.explore.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.dto.search.AdminSearchCriteria;
import ru.practicum.explore.dto.search.PublicSearchCriteria;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.ParticipationRequest;
import ru.practicum.explore.model.ParticipationRequestStatus;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.explore.mapper.EventState.PUBLISHED;

@Repository
@AllArgsConstructor
public class CustomEventRepository {

    private final EntityManager entityManager;

    public List<Event> findEventsPublic(PublicSearchCriteria publicSearchCriteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        Predicate publicationStatus = builder.equal(root.get("state"), PUBLISHED);
        predicates.add(publicationStatus);

        if (publicSearchCriteria.getText() != null) {
            String searchText = "%" + publicSearchCriteria.getText().toLowerCase() + "%";

            Predicate searchInAnnotation = builder.like(builder.lower(root.get("annotation")), searchText);
            Predicate searchInDescription = builder.like(builder.lower(root.get("description")), searchText);

            Predicate test = builder.or(searchInAnnotation, searchInDescription);
            predicates.add(test);
        }

        if (publicSearchCriteria.getCategories() != null) {
            Predicate categories = root.get("category").in(publicSearchCriteria.getCategories());
            predicates.add(categories);
        }

        if (publicSearchCriteria.getPaid() != null) {
            Predicate paid = builder.equal(root.get("paid"), publicSearchCriteria.getPaid());
            predicates.add(paid);
        }

        if (publicSearchCriteria.getRangeStart() != null) {
            Predicate rangeStart = builder.greaterThan(root.get("eventDate"), publicSearchCriteria.getRangeStart());
            predicates.add(rangeStart);
        }

        if (publicSearchCriteria.getRangeEnd() != null) {
            Predicate rangeEnd = builder.lessThan(root.get("eventDate"), publicSearchCriteria.getRangeEnd());
            predicates.add(rangeEnd);
        }

        if (publicSearchCriteria.getOnlyAvailable()) {
            Subquery<Long> sub = criteriaQuery.subquery(Long.class);
            Root<ParticipationRequest> subRoot = sub.from(ParticipationRequest.class);
            Join<ParticipationRequest, Event> subParticipation = subRoot.join("event");
            sub.select(builder.count(subRoot.get("event")));
            sub.where(builder.equal(root.get("id"), subParticipation.get("id")));
            sub.where(builder.equal(subRoot.get("confirmed"), ParticipationRequestStatus.CONFIRMED));
            Predicate onlyAvailable = builder.greaterThan(root.get("participantsLimit"), sub);

            predicates.add(onlyAvailable);
        }

        CriteriaQuery<Event> select = criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(publicSearchCriteria.getFrom());
        typedQuery.setMaxResults(publicSearchCriteria.getSize());

        return typedQuery.getResultList();
    }

    public List<Event> findEventsAdmin(AdminSearchCriteria adminSearchCriteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (adminSearchCriteria.getUsers() != null) {
            Predicate users = root.get("initiator").in(adminSearchCriteria.getUsers());
            predicates.add(users);
        }

        if (adminSearchCriteria.getStates() != null) {
            Predicate states = root.get("state").in(adminSearchCriteria.getStates());
            predicates.add(states);
        }

        if (adminSearchCriteria.getCategories() != null) {
            Predicate categories = root.get("category").in(adminSearchCriteria.getCategories());
            predicates.add(categories);
        }

        if (adminSearchCriteria.getRangeStart() != null) {
            Predicate rangeStart = builder.greaterThan(root.get("eventDate"), adminSearchCriteria.getRangeStart());
            predicates.add(rangeStart);
        }

        if (adminSearchCriteria.getRangeEnd() != null) {
            Predicate rangeEnd = builder.lessThan(root.get("eventDate"), adminSearchCriteria.getRangeEnd());
            predicates.add(rangeEnd);
        }

        CriteriaQuery<Event> select = criteriaQuery.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(adminSearchCriteria.getFrom());
        typedQuery.setMaxResults(adminSearchCriteria.getSize());

        return typedQuery.getResultList();
    }
}
