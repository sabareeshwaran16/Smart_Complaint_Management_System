package com.hostelcare.dto.request;

import com.hostelcare.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for updating the status of a complaint.
 */
@Data
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Status status;
}
