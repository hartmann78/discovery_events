package com.practice.events_service.controller.publicControllers;

import com.practice.events_service.dto.modelDTO.CategoryDTO;
import com.practice.events_service.service.publicService.PublicCategoriesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.practice.events_service.utils.SetLog.setLog;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoriesController {
    private final PublicCategoriesService publicCategoriesService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "10") int size,
                                                           HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(publicCategoriesService.getCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long catId,
                                                       HttpServletRequest request) {
        setLog(log, request);
        return new ResponseEntity<>(publicCategoriesService.getCategoryById(catId), HttpStatus.OK);
    }
}
