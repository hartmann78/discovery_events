package com.practice.events_service.controller.publicControllers;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.service.publicService.PublicCompilationsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.practice.events_service.utils.SetLog.setLog;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {
    private final PublicCompilationsService publicCompilationsService;

    @GetMapping
    public ResponseEntity<List<CompilationDTO>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                                @RequestParam(required = false, defaultValue = "10") int size,
                                                                HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(publicCompilationsService.getCompilations(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDTO> getCompilationById(@PathVariable Long compId,
                                                             HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(publicCompilationsService.getCompilationById(compId), HttpStatus.OK);
    }
}
