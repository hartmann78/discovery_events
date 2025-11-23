package com.practice.events_service.service.adminService;

import com.practice.events_service.dto.modelDTO.EventFullDTO;
import com.practice.events_service.dto.updateRequest.UpdateEventAdminRequest;
import com.practice.events_service.dto.updateRequest.UpdateEventCommentsState;

import java.util.List;

public interface AdminEventsService {
    List<EventFullDTO> getEvents(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, int from, int size);

    EventFullDTO patchEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDTO patchEventCommentsState(Long eventId, UpdateEventCommentsState updateEventCommentsState);
}
