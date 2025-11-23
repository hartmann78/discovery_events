package com.practice.events_service.service.privateService;

import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;

import java.util.List;

public interface PrivateRequestsService {
    List<ParticipationRequestDTO> getUserEventRequests(Long userId);

    ParticipationRequestDTO postEventParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDTO cancelEventParticipationRequest(Long userId, Long requestId);
}
