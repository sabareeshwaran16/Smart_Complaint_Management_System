package com.hostelcare.dto.response;

import com.hostelcare.enums.Priority;
import com.hostelcare.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for a Complaint — returned to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested summary objects
    private UserResponse user;
    private UserResponse assignedTo;
    private CategoryResponse category;
}
