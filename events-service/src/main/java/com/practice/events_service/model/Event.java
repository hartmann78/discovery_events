package com.practice.events_service.model;

import com.practice.events_service.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 120)
    private String title;

    @Column(name = "description", length = 7000)
    private String description;

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilation;

    @OneToMany(mappedBy = "event")
    private List<ParticipationRequest> participationRequests;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "views")
    private Long views;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @OneToMany(mappedBy = "event")
    private List<Comment> comments;

    @Column(name = "comments_available")
    private Boolean commentsAvailable;

    @Column(name = "show_comments")
    private Boolean showComments;

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", annotation='" + annotation + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", initiator=" + initiator +
                ", category=" + category +
                ", lat=" + lat +
                ", lon=" + lon +
                ", participantLimit=" + participantLimit +
                ", paid=" + paid +
                ", requestModeration=" + requestModeration +
                ", confirmedRequests=" + confirmedRequests +
                ", views=" + views +
                ", createdOn='" + createdOn + '\'' +
                ", publishedOn='" + publishedOn + '\'' +
                ", state=" + state +
                '}';
    }
}
