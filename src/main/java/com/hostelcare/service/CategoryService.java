package com.hostelcare.service;

import com.hostelcare.dto.request.CategoryRequest;
import com.hostelcare.dto.response.CategoryResponse;
import com.hostelcare.entity.Category;
import com.hostelcare.exception.DuplicateResourceException;
import com.hostelcare.exception.ResourceNotFoundException;
import com.hostelcare.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing complaint categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /** Create a new category. Throws if name already exists. */
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Category already exists: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    /** Retrieve all categories. */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Get category by ID — used internally by ComplaintService. */
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
