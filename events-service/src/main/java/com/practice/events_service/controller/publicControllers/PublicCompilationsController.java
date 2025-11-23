package com.practice.events_service.controller.publicControllers;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.service.publicService.PublicCompilationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {
    private final PublicCompilationsService publicCompilationsService;

    @GetMapping
    public ResponseEntity<List<CompilationDTO>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                                @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(publicCompilationsService.getCompilations(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDTO> getCompilationById(@PathVariable Long compId) {
        return new ResponseEntity<>(publicCompilationsService.getCompilationById(compId), HttpStatus.OK);
    }
}
