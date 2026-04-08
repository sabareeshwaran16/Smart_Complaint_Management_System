package com.hostelcare.controller;

import com.hostelcare.dto.request.AssignRequest;
import com.hostelcare.dto.request.ComplaintRequest;
import com.hostelcare.dto.request.StatusUpdateRequest;
import com.hostelcare.dto.response.ComplaintHistoryResponse;
import com.hostelcare.dto.response.ComplaintResponse;
import com.hostelcare.enums.Status;
import com.hostelcare.repository.UserRepository;
import com.hostelcare.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for Complaint management endpoints.
 *
 * POST   /api/complaints                    → Create complaint (STUDENT)
 * GET    /api/complaints                    → All complaints with filters (ADMIN/WARDEN)
 * GET    /api/complaints/{id}               → Single complaint (authenticated)
 * GET    /api/complaints/user/{userId}      → Complaints by user (STUDENT)
 * PUT    /api/complaints/{id}/status        → Update status (ADMIN/WARDEN)
 * PUT    /api/complaints/{id}/assign        → Assign to staff (ADMIN/WARDEN)
 * GET    /api/complaints/{id}/history       → Status history (authenticated)
 */
@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    /**
     * Create a new complaint.
     * Accepts multipart/form-data so that an optional image can be attached.
     *
     * Sample Form Fields:
     *   title        = "Water leakage in room 12"
     *   description  = "Pipe burst since morning"
     *   priority     = "HIGH"
     *   categoryId   = 1
     *   image        = <file> (optional)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComplaintResponse> createComplaint(
            @RequestPart("data") @Valid ComplaintRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Long userId = getUserIdFromEmail(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(complaintService.createComplaint(request, userId, image));
    }

    /**
     * Get all complaints — supports optional filtering by status and/or categoryId.
     *
     * Query params (all optional):
     *   ?status=PENDING
     *   ?categoryId=2
     *   ?status=PENDING&categoryId=2
     */
    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(complaintService.getAllComplaints(status, categoryId));
    }

    /**
     * Get a single complaint by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    /**
     * Get all complaints raised by a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(complaintService.getComplaintsByUser(userId));
    }

    /**
     * Update complaint status (ADMIN/WARDEN only).
     *
     * Sample Request Body:
     * { "status": "IN_PROGRESS" }
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request));
    }

    /**
     * Assign complaint to a staff member (ADMIN/WARDEN only).
     *
     * Sample Request Body:
     * { "assignedToId": 3 }
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<ComplaintResponse> assignComplaint(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request) {
        return ResponseEntity.ok(complaintService.assignComplaint(id, request));
    }

    /**
     * Get the full status-change history for a complaint.
     *
     * Sample Response:
     * [
     *   { "id": 2, "complaintId": 5, "status": "IN_PROGRESS", "updatedAt": "..." },
     *   { "id": 1, "complaintId": 5, "status": "PENDING",     "updatedAt": "..." }
     * ]
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ComplaintHistoryResponse>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintHistory(id));
    }

    // ─── Helper ──────────────────────────────────────────────────────────

    /** Resolve user ID from the authenticated email stored in JWT. */
    private Long getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow()
                .getId();
    }
}
