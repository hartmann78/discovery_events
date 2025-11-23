package com.practice.events_service.repositoryTests;

import com.practice.events_service.generators.CategoryGenerator;
import com.practice.events_service.model.Category;
import com.practice.events_service.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    private final CategoryGenerator categoryGenerator = new CategoryGenerator();

    private Category category;

    @BeforeEach
    void save() {
        category = categoryGenerator.generateCategory();
        categoryRepository.save(category);
    }

    @Test
    @Order(1)
    void findById() {
        Optional<Category> checkCategory = categoryRepository.findById(category.getId());
        assertTrue(checkCategory.isPresent());
        assertNotNull(checkCategory.get().getId());
        assertEquals(category, checkCategory.get());
    }

    @Test
    @Order(2)
    void checkCategoryNameExists() {
        Boolean check = categoryRepository.checkCategoryNameExists(category.getName());
        assertTrue(check);
    }

    @Test
    @Order(3)
    void checkCategoryIsAttachedToEvents() {
        assertFalse(categoryRepository.checkCategoryIsAttachedToEvents(category.getId()));
    }

    @Test
    @Order(4)
    void findAll() {
        List<Category> findAllCategories = categoryRepository.findAll();
        assertTrue(findAllCategories.contains(category));
    }

    @Test
    @Order(5)
    void update() {
        Category updateCategory = categoryGenerator.generateCategory();

        category.setName(updateCategory.getName());
        categoryRepository.save(category);

        Optional<Category> checkUpdatedCategory = categoryRepository.findById(category.getId());
        assertTrue(checkUpdatedCategory.isPresent());
        assertEquals(category.getId(), checkUpdatedCategory.get().getId());
        assertEquals(updateCategory.getName(), checkUpdatedCategory.get().getName());
    }

    @Test
    @Order(6)
    void delete() {
        categoryRepository.deleteById(category.getId());

        Optional<Category> checkCategory = categoryRepository.findById(category.getId());
        assertTrue(checkCategory.isEmpty());
    }
}
