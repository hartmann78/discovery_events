package com.practice.events_service.service.privateService;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.newDTO.NewEventDTO;
import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateRequest;
import com.practice.events_service.dto.eventRequestStatusUpdate.EventRequestStatusUpdateResult;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;
import com.practice.events_service.dto.updateRequest.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventsService {
    List<EventShortDTO> getEvents(Long userId, int from, int size);

    EventFullDTO getEventByUserId(Long userId, Long eventId);

    List<ParticipationRequestDTO> getEventRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventFullDTO addNewEvent(Long userId, NewEventDTO newEventDTO);

    EventFullDTO patchEventByUserId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult patchEventRequestsByUserId(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    EventFullDTO patchEventCommentsState(Long userId, Long eventId, UpdateEventCommentsState updateEventCommentsState);
}
