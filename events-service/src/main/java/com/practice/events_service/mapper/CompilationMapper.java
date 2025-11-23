package com.practice.events_service.mapper;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.shortDTO.EventShortDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;
import com.practice.events_service.model.Compilation;
import com.practice.events_service.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation createNewCompilation(NewCompilationDTO newCompilationDTO, List<Event> events) {
        boolean pinned;

        if (newCompilationDTO.getPinned() == null) {
            pinned = false;
        } else {
            pinned = newCompilationDTO.getPinned();
        }

        return Compilation.builder()
                .title(newCompilationDTO.getTitle())
                .pinned(pinned)
                .events(events)
                .build();
    }

    public CompilationDTO compilationToCompilationDTO(Compilation compilation) {
        List<EventShortDTO> eventShortDTOS = new ArrayList<>();

        for (Event event : compilation.getEvents()) {
            eventShortDTOS.add(eventMapper.eventToEventShortDTO(event));
        }

        return CompilationDTO.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventShortDTOS)
                .build();
    }

    public List<CompilationDTO> compilationListToCompilationDTOList(List<Compilation> compilations) {
        List<CompilationDTO> compilationDTOS = new ArrayList<>();

        for (Compilation compilation : compilations) {
            compilationDTOS.add(compilationToCompilationDTO(compilation));
        }

        return compilationDTOS;
    }

    public Compilation patchCompilationByUpdateCompilationRequest(Compilation compilation, UpdateCompilationRequest updateCompilationRequest, List<Event> events) {
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (!events.isEmpty()) {
            compilation.setEvents(events);
        }

        return compilation;
    }
}
