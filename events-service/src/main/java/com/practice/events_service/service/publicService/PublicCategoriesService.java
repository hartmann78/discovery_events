package com.practice.events_service.service.publicService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;

import java.util.List;

public interface PublicCategoriesService {
    List<CategoryDTO> getCategories(int from, int size);

    CategoryDTO getCategoryById(Long catId);
}
