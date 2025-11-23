package com.practice.events_service.service.adminService;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;

public interface AdminCategoriesService {
    CategoryDTO addNewCategory(NewCategoryDTO newCategoryDTO);

    CategoryDTO patchCategory(Long catId, UpdateCategoryRequest updateCategoryRequest);

    void deleteCategory(Long catId);
}
