package com.practice.events_service.controller.publicControllers;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.service.publicService.PublicCategoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoriesController {
    private final PublicCategoriesService publicCategoriesService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(publicCategoriesService.getCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long catId) {
        return new ResponseEntity<>(publicCategoriesService.getCategoryById(catId), HttpStatus.OK);
    }
}
