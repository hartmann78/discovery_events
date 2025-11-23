package com.practice.events_service.generators;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;
import com.practice.events_service.model.Category;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

@Component
public class CategoryGenerator {
    public Category generateCategory() {
        return Category.builder()
                .name(generateName())
                .build();
    }

    public NewCategoryDTO generateNewCategoryDTO() {
        return NewCategoryDTO.builder()
                .name(generateName())
                .build();
    }

    public CategoryDTO generateCategoryDTO() {
        return CategoryDTO.builder()
                .name(generateName())
                .build();
    }

    public UpdateCategoryRequest generateUpdateCategoryRequest() {
        return UpdateCategoryRequest.builder()
                .name(generateName())
                .build();
    }

    private String generateName() {
        return RandomString.make(8);
    }
}
