package com.example.loyalty.admin.infrastructure.persistence.repositories;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.entities.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for Report entity
 * Extends the domain repository interface
 */
@Repository
public interface JpaReportRepository extends JpaRepository<Report, UUID> {
    
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
     * Find reports by name containing (case insensitive search)
     */
    Page<Report> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Count reports by creator
     */
    long countByCreatedBy(UUID createdBy);
    
    /**
     * Find reports shared with user using JSON contains query
     */
    @Query(value = "SELECT r FROM Report r WHERE JSON_EXTRACT(r.sharedWith, '$.users') LIKE %:userId%")
    List<Report> findReportsSharedWithUser(@Param("userId") UUID userId);
    
    /**
     * Find reports that need to be regenerated (scheduled)
     * This would typically check schedule configuration and last generated time
     */
    @Query("SELECT r FROM Report r WHERE r.status = 'SCHEDULED' OR " +
           "(r.scheduleConfig IS NOT NULL AND " +
           "JSON_EXTRACT(r.scheduleConfig, '$.enabled') = 'true' AND " +
           "JSON_EXTRACT(r.scheduleConfig, '$.nextRun') <= CURRENT_TIMESTAMP)")
    List<Report> findReportsForScheduledGeneration();
}