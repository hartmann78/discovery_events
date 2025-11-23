package com.practice.events_service.controller.adminControllers;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;
import com.practice.events_service.service.adminService.AdminCompilationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final AdminCompilationsService adminCompilationsService;

    @PostMapping
    public ResponseEntity<CompilationDTO> postNewCompilation(@RequestBody @Valid NewCompilationDTO newCompilationDTO) {
        return new ResponseEntity<>(adminCompilationsService.postNewCompilation(newCompilationDTO), HttpStatus.CREATED);
    }


    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDTO> patchCompilation(@PathVariable Long compId,
                                                           @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return new ResponseEntity<>(adminCompilationsService.patchCompilation(compId, updateCompilationRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilation(@PathVariable Long compId) {
        adminCompilationsService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
