package com.practice.events_service.mapper;

import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateResult;
import com.practice.events_service.model.Event;
import com.practice.events_service.model.ParticipationRequest;
import com.practice.events_service.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParticipationRequestMapper {
    public ParticipationRequest createNewParticipationRequest(User requester, Event event) {
        return ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public ParticipationRequestDTO requestToRequestDTO(ParticipationRequest participationRequest) {
        return ParticipationRequestDTO.builder()
                .id(participationRequest.getId())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .created(participationRequest.getCreated())
                .status(participationRequest.getStatus().toString())
                .build();
    }

    public List<ParticipationRequestDTO> requestListToRequestDTOList(List<ParticipationRequest> requests) {
        List<ParticipationRequestDTO> requestDTOS = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            requestDTOS.add(requestToRequestDTO(request));
        }

        return requestDTOS;
    }

    public EventRequestStatusUpdateResult requestsListToEventRequestStatusUpdateResult(List<ParticipationRequest> requests) {
        List<ParticipationRequestDTO> confirmed = new ArrayList<>();
        List<ParticipationRequestDTO> rejected = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (request.getStatus() == ParticipationRequest.Status.CONFIRMED) {
                confirmed.add(requestToRequestDTO(request));
            } else {
                rejected.add(requestToRequestDTO(request));
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }
}
