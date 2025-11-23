package com.practice.events_service.dto.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDTO {
    private Long id;
    private Long requester;
    private Long event;
    private String created;
    private String status;
}
