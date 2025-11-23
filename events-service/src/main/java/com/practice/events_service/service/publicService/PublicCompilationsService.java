package com.practice.events_service.service.publicService;

import com.practice.events_service.dto.modelDTO.CompilationDTO;

import java.util.List;

public interface PublicCompilationsService {
    List<CompilationDTO> getCompilations(Boolean pinned, int from, int size);

    CompilationDTO getCompilationById(Long compId);
}
