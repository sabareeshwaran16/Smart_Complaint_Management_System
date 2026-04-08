package com.hostelcare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for assigning a complaint to a staff member.
 */
@Data
public class AssignRequest {

    @NotNull(message = "Assignee user ID is required")
    private Long assignedToId;
}
