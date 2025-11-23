package com.practice.events_service.service.adminService;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;

public interface AdminCompilationsService {
    CompilationDTO postNewCompilation(NewCompilationDTO newCompilationDTO);

    CompilationDTO patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(Long compId);
}
