package com.hostelcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for creating a complaint category.
 */
@Data
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;
}
