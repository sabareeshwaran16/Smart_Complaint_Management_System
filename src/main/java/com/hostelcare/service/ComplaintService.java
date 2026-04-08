package com.hostelcare.service;

import com.hostelcare.dto.request.AssignRequest;
import com.hostelcare.dto.request.ComplaintRequest;
import com.hostelcare.dto.request.StatusUpdateRequest;
import com.hostelcare.dto.response.CategoryResponse;
import com.hostelcare.dto.response.ComplaintHistoryResponse;
import com.hostelcare.dto.response.ComplaintResponse;
import com.hostelcare.dto.response.UserResponse;
import com.hostelcare.entity.Category;
import com.hostelcare.entity.Complaint;
import com.hostelcare.entity.ComplaintHistory;
import com.hostelcare.entity.User;
import com.hostelcare.enums.Status;
import com.hostelcare.exception.ResourceNotFoundException;
import com.hostelcare.exception.UnauthorizedException;
import com.hostelcare.repository.ComplaintHistoryRepository;
import com.hostelcare.repository.ComplaintRepository;
import com.hostelcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service handling all complaint business logic:
 *  - Creating complaints (with optional image upload)
 *  - Retrieving complaints (all, by user, by filters)
 *  - Updating complaint status (triggers email + history record)
 *  - Assigning complaints to staff members
 *  - Fetching complaint audit history
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    // ───────────────────── CREATE ─────────────────────────────────────────

    /**
     * Create a new complaint for the given user.
     * Optionally handles image file upload.
     */
    @Transactional
    public ComplaintResponse createComplaint(ComplaintRequest request,
                                              Long userId,
                                              MultipartFile image) throws IOException {
        User user = getUserById(userId);
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = fileStorageService.storeFile(image);
        }

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Status.PENDING)
                .imagePath(imagePath)
                .user(user)
                .category(category)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        // Save initial history record
        saveHistory(saved, Status.PENDING);

        log.info("Complaint #{} created by user #{}", saved.getId(), userId);
        return toResponse(saved);
    }

    // ───────────────────── READ ───────────────────────────────────────────

    /** Retrieve all complaints (Admin/Warden). Supports optional filtering. */
    public List<ComplaintResponse> getAllComplaints(Status status, Long categoryId) {
        List<Complaint> complaints;

        if (status != null && categoryId != null) {
            complaints = complaintRepository.findByStatusAndCategoryId(status, categoryId);
        } else if (status != null) {
            complaints = complaintRepository.findByStatus(status);
        } else if (categoryId != null) {
            complaints = complaintRepository.findByCategoryId(categoryId);
        } else {
            complaints = complaintRepository.findAll();
        }

        return complaints.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** Get all complaints belonging to a specific user (Student view). */
    public List<ComplaintResponse> getComplaintsByUser(Long userId) {
        getUserById(userId); // Validate user exists
        return complaintRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** Get a single complaint by ID. */
    public ComplaintResponse getComplaintById(Long id) {
        return toResponse(getComplaintEntityById(id));
    }

    /** Get the full status-change history for a complaint. */
    public List<ComplaintHistoryResponse> getComplaintHistory(Long complaintId) {
        getComplaintEntityById(complaintId); // validate exists
        return historyRepository.findByComplaintIdOrderByUpdatedAtDesc(complaintId)
                .stream()
                .map(h -> ComplaintHistoryResponse.builder()
                        .id(h.getId())
                        .complaintId(complaintId)
                        .status(h.getStatus())
                        .updatedAt(h.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ───────────────────── UPDATE ─────────────────────────────────────────

    /**
     * Update the status of a complaint.
     * Records history and sends email notification to the complaint owner.
     */
    @Transactional
    public ComplaintResponse updateStatus(Long id, StatusUpdateRequest request) {
        Complaint complaint = getComplaintEntityById(id);
        complaint.setStatus(request.getStatus());
        Complaint updated = complaintRepository.save(complaint);

        // Record status change in history
        saveHistory(updated, request.getStatus());

        // Send async email notification
        emailService.sendStatusChangeNotification(
                complaint.getUser().getEmail(),
                complaint.getUser().getName(),
                complaint.getId(),
                request.getStatus().name()
        );

        log.info("Complaint #{} status updated to {}", id, request.getStatus());
        return toResponse(updated);
    }

    /**
     * Assign a complaint to a staff member (WARDEN/ADMIN).
     * Sends an email to the assigned staff member.
     */
    @Transactional
    public ComplaintResponse assignComplaint(Long id, AssignRequest request) {
        Complaint complaint = getComplaintEntityById(id);
        User assignee = getUserById(request.getAssignedToId());

        // Only ADMIN or WARDEN can be assigned
        if (assignee.getRole().name().equals("STUDENT")) {
            throw new UnauthorizedException("Cannot assign complaint to a student.");
        }

        complaint.setAssignedTo(assignee);
        Complaint updated = complaintRepository.save(complaint);

        // Notify assignee
        emailService.sendAssignmentNotification(
                assignee.getEmail(),
                assignee.getName(),
                complaint.getId(),
                complaint.getTitle()
        );

        log.info("Complaint #{} assigned to user #{}", id, request.getAssignedToId());
        return toResponse(updated);
    }

    // ───────────────────── HELPERS ────────────────────────────────────────

    private Complaint getComplaintEntityById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void saveHistory(Complaint complaint, Status status) {
        ComplaintHistory history = ComplaintHistory.builder()
                .complaint(complaint)
                .status(status)
                .build();
        historyRepository.save(history);
    }

    /** Map Complaint entity → ComplaintResponse DTO. */
    private ComplaintResponse toResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .status(c.getStatus())
                .priority(c.getPriority())
                .imagePath(c.getImagePath())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .user(toUserResponse(c.getUser()))
                .assignedTo(c.getAssignedTo() != null ? toUserResponse(c.getAssignedTo()) : null)
                .category(CategoryResponse.builder()
                        .id(c.getCategory().getId())
                        .name(c.getCategory().getName())
                        .build())
                .build();
    }

    private UserResponse toUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }
}
