package com.hostelcare.dto.response;

import com.hostelcare.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for a single history record of a complaint's status change.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintHistoryResponse {
    private Long id;
    private Long complaintId;
    private Status status;
    private LocalDateTime updatedAt;
}
