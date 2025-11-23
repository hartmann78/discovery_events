package com.practice.events_service.dto.shortDTO;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDTO {
    private Long id;
    private String title;
    private String annotation;
    private String eventDate;
    private UserShortDTO initiator;
    private CategoryDTO category;
    private Boolean paid;
    private Long confirmedRequests;
    private Long views;
}

