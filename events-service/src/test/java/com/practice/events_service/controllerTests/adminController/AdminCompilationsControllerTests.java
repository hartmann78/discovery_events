package com.practice.events_service.controllerTests.adminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.modelDTO.CompilationDTO;
import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;
import com.practice.events_service.generators.CompilationGenerator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminCompilationsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CompilationGenerator compilationGenerator;

    private Long compilationId;

    @Test
    @BeforeEach
    void postNewCompilation() throws Exception {
        NewCompilationDTO newCompilationDTO = compilationGenerator.generateNewCompilationDTO(Collections.emptyList());
        String newCompilationDTOJson = objectMapper.writeValueAsString(newCompilationDTO);

        ResultActions compilationResult = mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCompilationDTOJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(newCompilationDTO.getTitle()))
                .andExpect(jsonPath("$.pinned").value(newCompilationDTO.getPinned()))
                .andExpect(jsonPath("$.events").exists());

        compilationId = objectMapper.readValue(compilationResult.andReturn().getResponse().getContentAsString(), CompilationDTO.class).getId();
    }

    @Test
    void patchCompilation() throws Exception {
        UpdateCompilationRequest updateCompilation = compilationGenerator.generateUpdateCompilationRequest(Collections.emptyList());
        String updateCompilationJson = objectMapper.writeValueAsString(updateCompilation);

        mockMvc.perform(patch("/admin/compilations/{compId}", compilationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCompilationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(compilationId))
                .andExpect(jsonPath("$.title").value(updateCompilation.getTitle()))
                .andExpect(jsonPath("$.pinned").value(updateCompilation.getPinned()))
                .andExpect(jsonPath("$.events").exists());
    }

    @Test
    void deleteCompilation() throws Exception {
        mockMvc.perform(delete("/admin/compilations/{compId}", compilationId))
                .andExpect(status().isNoContent());
    }
}
