package com.practice.events_service.dto.modelDTO;

import com.practice.events_service.dto.shortDTO.EventShortDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDTO {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDTO> events;
}
