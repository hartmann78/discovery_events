package com.practice.events_service.service.publicService.impl;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.mapper.CompilationMapper;
import com.practice.events_service.model.Compilation;
import com.practice.events_service.repository.CompilationRepository;
import com.practice.events_service.service.publicService.PublicCompilationsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCompilationsServiceImpl implements PublicCompilationsService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CheckService checkService;

    @Override
    public List<CompilationDTO> getCompilations(Boolean pinned, int from, int size) {
        checkService.fromAndSizeCheck(from, size);
        List<Compilation> compilations = compilationRepository.getCompilations(pinned, from, size);

        return compilationMapper.compilationListToCompilationDTOList(compilations);
    }

    @Override
    public CompilationDTO getCompilationById(Long compId) {
        Compilation compilation = checkService.findCompilation(compId);

        return compilationMapper.compilationToCompilationDTO(compilation);
    }
}
