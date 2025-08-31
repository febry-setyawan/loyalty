package com.example.loyalty.admin.infrastructure.persistence.repositories;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.entities.ReportStatus;
import com.example.loyalty.admin.domain.repositories.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ReportRepository using JPA
 * Bridges domain repository interface with JPA repository
 */
@Repository
public class ReportRepositoryImpl implements ReportRepository {
    
    private final JpaReportRepository jpaReportRepository;
    
    public ReportRepositoryImpl(JpaReportRepository jpaReportRepository) {
        this.jpaReportRepository = jpaReportRepository;
    }
    
    @Override
    public Report save(Report report) {
        return jpaReportRepository.save(report);
    }
    
    @Override
    public Optional<Report> findById(UUID id) {
        return jpaReportRepository.findById(id);
    }
    
    @Override
    public Page<Report> findAll(Pageable pageable) {
        return jpaReportRepository.findAll(pageable);
    }
    
    @Override
    public Page<Report> findByCreatedBy(UUID createdBy, Pageable pageable) {
        return jpaReportRepository.findByCreatedBy(createdBy, pageable);
    }
    
    @Override
    public List<Report> findByType(ReportType type) {
        return jpaReportRepository.findByType(type);
    }
    
    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return jpaReportRepository.findByStatus(status);
    }
    
    @Override
    public Page<Report> findByIsPublicTrue(Pageable pageable) {
        return jpaReportRepository.findByIsPublicTrue(pageable);
    }
    
    @Override
    public List<Report> findReportsSharedWithUser(UUID userId) {
        return jpaReportRepository.findReportsSharedWithUser(userId);
    }
    
    @Override
    public Page<Report> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return jpaReportRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaReportRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaReportRepository.existsById(id);
    }
    
    @Override
    public long countByCreatedBy(UUID createdBy) {
        return jpaReportRepository.countByCreatedBy(createdBy);
    }
    
    @Override
    public List<Report> findReportsForScheduledGeneration() {
        return jpaReportRepository.findReportsForScheduledGeneration();
    }
}