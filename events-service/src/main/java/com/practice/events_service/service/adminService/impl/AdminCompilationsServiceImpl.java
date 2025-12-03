package com.practice.events_service.service.adminService.impl;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;
import com.practice.events_service.mapper.CompilationMapper;
import com.practice.events_service.model.Compilation;
import com.practice.events_service.model.Event;
import com.practice.events_service.repository.CompilationRepository;
import com.practice.events_service.service.adminService.AdminCompilationsService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompilationsServiceImpl implements AdminCompilationsService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CheckService checkService;

    @Override
    public CompilationDTO postNewCompilation(NewCompilationDTO newCompilationDTO) {
        List<Event> events = Optional.ofNullable(newCompilationDTO.getEvents())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(checkService::findEvent)
                .collect(Collectors.toList());

        Compilation compilation = compilationMapper.createNewCompilation(newCompilationDTO, events);
        compilationRepository.save(compilation);

        return compilationMapper.compilationToCompilationDTO(compilation);
    }

    @Override
    public CompilationDTO patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        List<Event> events = Optional.ofNullable(updateCompilationRequest.getEvents())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(checkService::findEvent)
                .collect(Collectors.toList());

        Compilation compilation = compilationMapper.patchCompilationByUpdateCompilationRequest
                (checkService.findCompilation(compId), updateCompilationRequest, events);
        compilationRepository.save(compilation);

        return compilationMapper.compilationToCompilationDTO(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        checkService.findCompilation(compId);

        compilationRepository.deleteById(compId);
    }
}
