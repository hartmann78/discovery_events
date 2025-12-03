package com.practice.events_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "created")
    private LocalDateTime created;

    public enum Status {
        PENDING,
        CANCELED,
        REJECTED,
        CONFIRMED
    }

    @Override
    public String toString() {
        return "ParticipationRequest{" +
                "id=" + id +
                ", requester=" + requester +
                ", event=" + event +
                ", status=" + status +
                ", created='" + created + '\'' +
                '}';
    }
}
