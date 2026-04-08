package com.hostelcare.controller;

import com.hostelcare.dto.request.CategoryRequest;
import com.hostelcare.dto.response.CategoryResponse;
import com.hostelcare.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category endpoints.
 *
 * GET  /api/categories      → List all categories (authenticated)
 * POST /api/categories      → Create a category (ADMIN only)
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Get all available complaint categories.
     *
     * Sample Response:
     * [
     *   { "id": 1, "name": "Plumbing" },
     *   { "id": 2, "name": "Electrical" }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Create a new complaint category (ADMIN only).
     *
     * Sample Request Body:
     * { "name": "Cleanliness" }
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }
}
