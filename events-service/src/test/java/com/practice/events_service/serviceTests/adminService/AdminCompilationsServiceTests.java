package com.practice.events_service.serviceTests.adminService;

import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;
import com.practice.events_service.exception.not_found.CompilationNotFoundException;
import com.practice.events_service.generators.CompilationGenerator;
import com.practice.events_service.service.adminService.AdminCompilationsService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminCompilationsServiceTests {
    @Autowired
    private AdminCompilationsService adminCompilationsService;

    @Autowired
    private CompilationGenerator compilationGenerator;

    private static CompilationDTO compilationDTO;

    @Test
    @Order(1)
    void postNewCompilation() {
        NewCompilationDTO newCompilationDTO = compilationGenerator.generateNewCompilationDTO(Collections.emptyList());
        compilationDTO = adminCompilationsService.postNewCompilation(newCompilationDTO);

        assertNotNull(compilationDTO.getId());
        assertEquals(newCompilationDTO.getTitle(), compilationDTO.getTitle());
        assertEquals(newCompilationDTO.getPinned(), compilationDTO.getPinned());
        assertEquals(0, compilationDTO.getEvents().size());
    }

    @Test
    @Order(2)
    void patchCompilation() {
        UpdateCompilationRequest updateCompilationRequest = compilationGenerator.generateUpdateCompilationRequest(Collections.emptyList());
        CompilationDTO updatedCompilationDTO = adminCompilationsService.patchCompilation(compilationDTO.getId(), updateCompilationRequest);

        assertEquals(compilationDTO.getId(), updatedCompilationDTO.getId());
        assertEquals(updateCompilationRequest.getTitle(), updatedCompilationDTO.getTitle());
        assertEquals(updateCompilationRequest.getPinned(), updatedCompilationDTO.getPinned());
        assertEquals(0, updatedCompilationDTO.getEvents().size());
    }

    @Test
    @Order(3)
    void deleteCompilation() {
        adminCompilationsService.deleteCompilation(compilationDTO.getId());

        assertThrows(CompilationNotFoundException.class, () -> adminCompilationsService.deleteCompilation(compilationDTO.getId()));
    }
}
