package com.practice.events_service.generators;

import com.practice.events_service.model.Event;
import com.practice.events_service.model.ParticipationRequest;
import com.practice.events_service.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ParticipationRequestGenerator {
    public ParticipationRequest generateParticipationRequest(User requestor, Event event) {
        return ParticipationRequest.builder()
                .requester(requestor)
                .event(event)
                .created(LocalDateTime.now())
                .status(ParticipationRequest.Status.PENDING)
                .build();
    }
}
