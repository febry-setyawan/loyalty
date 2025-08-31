package com.example.loyalty.admin.domain.repositories;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.entities.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Report domain entity
 * Defines data access operations for reports
 */
public interface ReportRepository {
    
    /**
     * Save or update a report
     */
    Report save(Report report);
    
    /**
     * Find report by ID
     */
    Optional<Report> findById(UUID id);
    
    /**
     * Find all reports with pagination
     */
    Page<Report> findAll(Pageable pageable);
    
    /**
     * Find reports by creator with pagination
     */
    Page<Report> findByCreatedBy(UUID createdBy, Pageable pageable);
    
    /**
     * Find reports by type
     */
    List<Report> findByType(ReportType type);
    
    /**
     * Find reports by status
     */
    List<Report> findByStatus(ReportStatus status);
    
    /**
     * Find public reports
     */
    Page<Report> findByIsPublicTrue(Pageable pageable);
    
    /**
     * Find reports shared with user
     */
    List<Report> findReportsSharedWithUser(UUID userId);
    
    /**
     * Find reports by name containing (case insensitive search)
     */
    Page<Report> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Delete report by ID
     */
    void deleteById(UUID id);
    
    /**
     * Check if report exists by ID
     */
    boolean existsById(UUID id);
    
    /**
     * Count reports by creator
     */
    long countByCreatedBy(UUID createdBy);
    
    /**
     * Find reports that need to be regenerated (scheduled)
     */
    List<Report> findReportsForScheduledGeneration();
}