package com.practice.events_service.dto.eventRequestStatusUpdate;

import com.practice.events_service.dto.modelDTO.ParticipationRequestDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDTO> confirmedRequests;
    private List<ParticipationRequestDTO> rejectedRequests;
}
