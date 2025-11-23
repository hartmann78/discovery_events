package com.practice.events_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 250)
    private String name;

    @Column(name = "email", length = 254)
    private String email;

    @OneToMany(mappedBy = "initiator")
    private List<Event> events;

    @OneToMany(mappedBy = "requester")
    private List<ParticipationRequest> participationRequests;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
