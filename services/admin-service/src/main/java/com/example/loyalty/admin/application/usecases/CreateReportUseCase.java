package com.example.loyalty.admin.application.usecases;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.repositories.ReportRepository;
import com.example.loyalty.admin.application.dto.CreateReportRequest;
import com.example.loyalty.admin.application.dto.ReportDTO;
import com.example.loyalty.common.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/**
 * Use case for creating new reports
 * Handles validation and business rules for report creation
 */
@Service
@Transactional
public class CreateReportUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateReportUseCase.class);
    
    private final ReportRepository reportRepository;
    
    public CreateReportUseCase(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    /**
     * Execute the create report use case
     * 
     * @param request The report creation request
     * @return Created report DTO
     * @throws ValidationException if validation fails
     */
    public ReportDTO execute(CreateReportRequest request) {
        // Validate input first
        validateRequest(request);
        
        logger.info("Creating new report: {}", request.getName());
        
        try {
            // Create report entity
            Report report = new Report(
                request.getName(),
                request.getType(),
                request.getCreatedBy()
            );
            
            // Set optional fields
            if (request.getDescription() != null) {
                report.setDescription(request.getDescription());
            }
            
            if (request.getConfiguration() != null) {
                report.updateConfiguration(request.getConfiguration());
            }
            
            if (request.getFilters() != null) {
                report.updateFilters(request.getFilters());
            }
            
            if (request.getStatisticalConfig() != null) {
                report.updateStatisticalConfig(request.getStatisticalConfig());
            }
            
            // Save report
            Report savedReport = reportRepository.save(report);
            
            logger.info("Successfully created report with ID: {}", savedReport.getId());
            
            return ReportDTO.from(savedReport);
            
        } catch (Exception e) {
            logger.error("Failed to create report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create report", e);
        }
    }
    
    private void validateRequest(CreateReportRequest request) {
        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Report name is required");
        }
        
        if (request.getName().length() > 255) {
            throw new ValidationException("Report name is too long");
        }
        
        if (request.getType() == null) {
            throw new ValidationException("Report type is required");
        }
        
        if (request.getCreatedBy() == null) {
            throw new ValidationException("Creator ID is required");
        }
        
        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            throw new ValidationException("Description is too long");
        }
    }
}