package com.practice.events_service.mapper;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;
import com.practice.events_service.model.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryMapper {
    public Category createNewCategory(NewCategoryDTO newCategoryDTO) {
        return Category.builder()
                .name(newCategoryDTO.getName())
                .build();
    }

    public CategoryDTO categoryToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public List<CategoryDTO> categoryListToCategoryDTOList(List<Category> categories) {
        List<CategoryDTO> categoryDTOS = new ArrayList<>();

        for (Category category : categories) {
            categoryDTOS.add(categoryToCategoryDTO(category));
        }

        return categoryDTOS;
    }

    public Category patchCategoryByCategoryDTO(Category category, UpdateCategoryRequest updateCategoryRequest) {
        category.setName(updateCategoryRequest.getName());
        return category;
    }
}
