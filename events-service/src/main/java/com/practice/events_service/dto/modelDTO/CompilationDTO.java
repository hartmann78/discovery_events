package com.practice.events_service.dto.modelDTO;

import com.practice.events_service.dto.shortDTO.EventShortDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDTO {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDTO> events;
}
