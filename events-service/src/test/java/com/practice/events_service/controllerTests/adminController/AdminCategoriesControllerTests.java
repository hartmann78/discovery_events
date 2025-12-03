package com.practice.events_service.controllerTests.adminController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;
import com.practice.events_service.generators.CategoryGenerator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminCategoriesControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryGenerator categoryGenerator;

    private Long categoryId;

    @Test
    @BeforeEach
    void addNewCategory() throws Exception {
        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        String newCategoryDTOJson = objectMapper.writeValueAsString(newCategoryDTO);

        ResultActions categoryResults = mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryDTOJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(newCategoryDTO.getName()));

        categoryId = objectMapper.readValue(categoryResults.andReturn().getResponse().getContentAsString(), CategoryDTO.class).getId();
    }

    @Test
    void patchCategory() throws Exception {
        UpdateCategoryRequest updateCategoryRequest = categoryGenerator.generateUpdateCategoryRequest();
        String patchCategoryJson = objectMapper.writeValueAsString(updateCategoryRequest);

        mockMvc.perform(patch("/admin/categories/{catId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchCategoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value(updateCategoryRequest.getName()));

    }

    @Test
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/admin/categories/{catId}", categoryId))
                .andExpect(status().isNoContent());
    }
}
