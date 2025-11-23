package com.practice.events_service.controller.adminControllers;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.dto.newDTO.NewCategoryDTO;
import com.practice.events_service.dto.updateRequest.UpdateCategoryRequest;
import com.practice.events_service.service.adminService.AdminCategoriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoriesController {
    private final AdminCategoriesService adminCategoriesService;

    @PostMapping
    public ResponseEntity<CategoryDTO> addNewCategory(@RequestBody @Valid NewCategoryDTO newCategoryDTO) {
        return new ResponseEntity<>(adminCategoriesService.addNewCategory(newCategoryDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDTO> patchCategory(@PathVariable Long catId,
                                                     @RequestBody @Valid UpdateCategoryRequest updateCategoryRequest) {
        return new ResponseEntity<>(adminCategoriesService.patchCategory(catId, updateCategoryRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable Long catId) {
        adminCategoriesService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
