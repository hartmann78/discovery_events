package com.practice.events_service.dto.shortDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.events_service.dto.modelDTO.CategoryDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDTO {
    private Long id;
    private String title;
    private String annotation;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDTO initiator;
    private CategoryDTO category;
    private Boolean paid;
    private Long confirmedRequests;
    private Long views;
}

