package com.practice.events_service.generators;

import com.practice.events_service.model.Event;
import com.practice.events_service.model.ParticipationRequest;
import com.practice.events_service.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ParticipationRequestGenerator {
    public ParticipationRequest generateParticipationRequest(User requestor, Event event) {
        return ParticipationRequest.builder()
                .requester(requestor)
                .event(event)
                .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .status(ParticipationRequest.Status.PENDING)
                .build();
    }
}
