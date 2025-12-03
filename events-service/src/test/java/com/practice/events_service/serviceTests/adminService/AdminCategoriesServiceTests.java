package com.practice.events_service.serviceTests.adminService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;
import com.practice.events_service.exception.not_found.CategoryNotFoundException;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AdminCategoriesServiceTests {
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private CategoryGenerator categoryGenerator;

    private CategoryDTO categoryDTO;

    @Test
    @BeforeEach
    void addNewCategory() {
        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);
        assertNotNull(categoryDTO.getId());
    }

    @Test
    void patchCategory() {
        UpdateCategoryRequest updateCategoryRequest = categoryGenerator.generateUpdateCategoryRequest();
        CategoryDTO updatedCategory = adminCategoriesService.patchCategory(categoryDTO.getId(), updateCategoryRequest);

        assertEquals(categoryDTO.getId(), updatedCategory.getId());
        assertEquals(updateCategoryRequest.getName(), updatedCategory.getName());
    }

    @Test
    void deleteCategory() {
        adminCategoriesService.deleteCategory(categoryDTO.getId());

        assertThrows(CategoryNotFoundException.class, () -> adminCategoriesService.deleteCategory(categoryDTO.getId()));
    }
}
