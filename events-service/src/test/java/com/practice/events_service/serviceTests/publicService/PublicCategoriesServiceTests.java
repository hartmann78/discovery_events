package com.practice.events_service.serviceTests.publicService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.service.publicService.PublicCategoriesService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicCategoriesServiceTests {
    @Autowired
    private AdminCategoriesService adminCategoriesService;
    @Autowired
    private PublicCategoriesService publicCategoriesService;

    @Autowired
    private CategoryGenerator categoryGenerator;

    private static CategoryDTO categoryDTO;

    @Test
    @Order(1)
    void getCategories() {
        NewCategoryDTO newCategoryDTO = categoryGenerator.generateNewCategoryDTO();
        categoryDTO = adminCategoriesService.addNewCategory(newCategoryDTO);

        assertNotNull(categoryDTO.getId());
        assertEquals(newCategoryDTO.getName(), categoryDTO.getName());

        List<CategoryDTO> categoryDTOS = publicCategoriesService.getCategories(0, 10);
        assertFalse(categoryDTOS.isEmpty());
    }

    @Test
    @Order(2)
    void getCategoryById() {
        CategoryDTO checkCategory = publicCategoriesService.getCategoryById(categoryDTO.getId());

        assertEquals(categoryDTO.getId(), checkCategory.getId());
        assertEquals(categoryDTO.getName(), checkCategory.getName());
    }
}
