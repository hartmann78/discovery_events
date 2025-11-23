package com.practice.events_service.service.publicService.impl;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.mapper.CategoryMapper;
import com.practice.events_service.model.Category;
import com.practice.events_service.repository.CategoryRepository;
import com.practice.events_service.service.publicService.PublicCategoriesService;
import com.practice.events_service.utils.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCategoriesServiceImpl implements PublicCategoriesService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CheckService checkService;

    @Override
    public List<CategoryDTO> getCategories(int from, int size) {
        checkService.fromAndSizeCheck(from, size);
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).getContent();

        return categoryMapper.categoryListToCategoryDTOList(categories);
    }

    @Override
    public CategoryDTO getCategoryById(Long catId) {
        Category category = checkService.findCategory(catId);

        return categoryMapper.categoryToCategoryDTO(category);
    }
}
