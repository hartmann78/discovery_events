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
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    private final CategoryGenerator categoryGenerator = new CategoryGenerator();

    private Category category;

    @Test
    @BeforeEach
    void save() {
        category = categoryGenerator.generateCategory();
        categoryRepository.save(category);
    }

    @Test
    void findById() {
        Optional<Category> checkCategory = categoryRepository.findById(category.getId());
        assertTrue(checkCategory.isPresent());
        assertNotNull(checkCategory.get().getId());
        assertEquals(category, checkCategory.get());
    }

    @Test
    void findAll() {
        List<Category> findAllCategories = categoryRepository.findAll();
        assertTrue(findAllCategories.contains(category));
    }

    @Test
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
    void delete() {
        categoryRepository.deleteById(category.getId());

        Optional<Category> checkCategory = categoryRepository.findById(category.getId());
        assertTrue(checkCategory.isEmpty());
    }

    @Test
    void checkCategoryNameExists() {
        Boolean check = categoryRepository.checkCategoryNameExists(category.getName());
        assertTrue(check);
    }

    @Test
    void checkCategoryIsAttachedToEvents() {
        assertFalse(categoryRepository.checkCategoryIsAttachedToEvents(category.getId()));
    }
}
