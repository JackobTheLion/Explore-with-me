package ru.practicum.explore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.mapper.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User initiator;

    private String title;

    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Boolean paid;

    private Long participantsLimit;

    private Boolean requestModeration;

    @Column(name = "event_state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    private LocalDateTime publishedOn = LocalDateTime.now();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private List<ParticipationRequest> participationRequests;

    @Override
    public String toString() {
        int numberOfRequests = 0;
        if (participationRequests != null) {
            numberOfRequests = participationRequests.size();
        }
        return "Event{" +
                "id=" + id +
                ", createdOn=" + createdOn +
                ", initiator=" + initiator +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", location=" + location +
                ", paid=" + paid +
                ", participantsLimit=" + participantsLimit +
                ", requestModeration=" + requestModeration +
                ", state=" + state +
                ", publishedOn=" + publishedOn +
                ", participationRequests number=" + numberOfRequests +
                '}';
    }
}
