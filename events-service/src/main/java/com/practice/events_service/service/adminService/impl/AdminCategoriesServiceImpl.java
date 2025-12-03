package com.practice.events_service.service.adminService.impl;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;
import com.practice.events_service.exception.conflict.CategoryAttachedToEventsException;
import com.practice.events_service.exception.conflict.CategoryNameExistsException;
import com.practice.events_service.mapper.CategoryMapper;
import com.practice.events_service.model.Category;
import com.practice.events_service.repository.CategoryRepository;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCategoriesServiceImpl implements AdminCategoriesService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CheckService checkService;

    @Override
    public CategoryDTO addNewCategory(NewCategoryDTO newCategoryDTO) {
        if (categoryRepository.checkCategoryNameExists(newCategoryDTO.getName())) {
            throw new CategoryNameExistsException("Название категории " + newCategoryDTO.getName() + " уже занята!");
        }

        Category newCategory = categoryMapper.createNewCategory(newCategoryDTO);
        categoryRepository.save(newCategory);

        return categoryMapper.categoryToCategoryDTO(newCategory);
    }

    @Override
    public CategoryDTO patchCategory(Long catId, UpdateCategoryRequest updateCategoryRequest) {
        Category category = checkService.findCategory(catId);
        String newCategoryName = updateCategoryRequest.getName();

        if (!category.getName().equals(newCategoryName) && categoryRepository.checkCategoryNameExists(newCategoryName)) {
            throw new CategoryNameExistsException("Название категории " + updateCategoryRequest.getName() + " уже занята!");
        }

        category = categoryMapper.patchCategoryByCategoryDTO(category, updateCategoryRequest);
        categoryRepository.save(category);

        return categoryMapper.categoryToCategoryDTO(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        checkService.findCategory(catId);

        if (categoryRepository.checkCategoryIsAttachedToEvents(catId)) {
            throw new CategoryAttachedToEventsException("Данная категория привязана к существующим событиям!");
        }

        categoryRepository.deleteById(catId);
    }
}
