package com.hostelcare.repository;

import com.hostelcare.entity.Complaint;
import com.hostelcare.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Complaint entity.
 * Supports filtering by user, status, and category.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    /** Fetch all complaints raised by a specific user. */
    List<Complaint> findByUserId(Long userId);

    /** Filter complaints by status. */
    List<Complaint> findByStatus(Status status);

    /** Filter complaints by category. */
    List<Complaint> findByCategoryId(Long categoryId);

    /** Filter complaints by both status and category. */
    List<Complaint> findByStatusAndCategoryId(Status status, Long categoryId);
}
