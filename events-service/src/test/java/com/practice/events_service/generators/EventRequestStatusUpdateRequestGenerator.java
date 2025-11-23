package com.practice.events_service.generators;

import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventRequestStatusUpdateRequestGenerator {
    public EventRequestStatusUpdateRequest generateUpdateRequest(List<Long> requestIds, EventRequestStatusUpdateRequest.Status status) {
        return EventRequestStatusUpdateRequest.builder()
                .requestIds(requestIds)
                .status(status)
                .build();
    }
}
