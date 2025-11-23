package com.practice.events_service.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "created")
    private String created;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

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
                ", created='" + created + '\'' +
                ", status=" + status +
                ", requester=" + requester +
                ", event=" + event +
                '}';
    }
}
