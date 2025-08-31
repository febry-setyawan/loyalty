package com.example.loyalty.admin.interfaces.web.controllers;

import com.example.loyalty.admin.application.usecases.CreateReportUseCase;
import com.example.loyalty.admin.application.usecases.GenerateReportUseCase;
import com.example.loyalty.admin.application.dto.CreateReportRequest;
import com.example.loyalty.admin.application.dto.ReportDTO;
import com.example.loyalty.admin.domain.repositories.ReportRepository;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.common.response.ApiResponse;
import com.example.loyalty.common.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for advanced reporting capabilities
 * Provides endpoints for report creation, generation, and management
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
@Validated
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    private final CreateReportUseCase createReportUseCase;
    private final GenerateReportUseCase generateReportUseCase;
    private final ReportRepository reportRepository;
    
    public ReportController(
            CreateReportUseCase createReportUseCase,
            GenerateReportUseCase generateReportUseCase,
            ReportRepository reportRepository) {
        this.createReportUseCase = createReportUseCase;
        this.generateReportUseCase = generateReportUseCase;
        this.reportRepository = reportRepository;
    }
    
    /**
     * Create a new report
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO>> createReport(
            @Valid @RequestBody CreateReportRequest request) {
        
        logger.info("Creating new report: {}", request.getName());
        
        try {
            ReportDTO report = createReportUseCase.execute(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(report, "Report created successfully"));
                    
        } catch (Exception e) {
            logger.error("Failed to create report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to create report: " + e.getMessage()));
        }
    }
    
    /**
     * Generate an existing report
     */
    @PostMapping("/{reportId}/generate")
    public ResponseEntity<ApiResponse<ReportDTO>> generateReport(
            @PathVariable UUID reportId) {
        
        logger.info("Generating report with ID: {}", reportId);
        
        try {
            ReportDTO report = generateReportUseCase.execute(reportId);
            
            return ResponseEntity.ok(
                    ApiResponse.success(report, "Report generated successfully"));
                    
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Report not found"));
        } catch (Exception e) {
            logger.error("Failed to generate report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to generate report: " + e.getMessage()));
        }
    }
    
    /**
     * Get all reports with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getAllReports(
            Pageable pageable) {
        
        logger.info("Fetching all reports with pagination: {}", pageable);
        
        try {
            Page<ReportDTO> reports = reportRepository.findAll(pageable)
                    .map(ReportDTO::summary); // Use summary to avoid large data transfer
            
            return ResponseEntity.ok(
                    ApiResponse.success(reports, "Reports retrieved successfully"));
                    
        } catch (Exception e) {
            logger.error("Failed to fetch reports", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to fetch reports: " + e.getMessage()));
        }
    }
    
    /**
     * Get report by ID
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportDTO>> getReport(
            @PathVariable UUID reportId) {
        
        logger.info("Fetching report with ID: {}", reportId);
        
        try {
            ReportDTO report = reportRepository.findById(reportId)
                    .map(ReportDTO::from)
                    .orElseThrow(() -> new NotFoundException("Report not found"));
            
            return ResponseEntity.ok(
                    ApiResponse.success(report, "Report retrieved successfully"));
                    
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("NOT_FOUND", "Report not found"));
        } catch (Exception e) {
            logger.error("Failed to fetch report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to fetch report: " + e.getMessage()));
        }
    }
    
    /**
     * Get reports by creator
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByCreator(
            @PathVariable UUID creatorId,
            Pageable pageable) {
        
        logger.info("Fetching reports for creator: {}", creatorId);
        
        try {
            Page<ReportDTO> reports = reportRepository.findByCreatedBy(creatorId, pageable)
                    .map(ReportDTO::summary);
            
            return ResponseEntity.ok(
                    ApiResponse.success(reports, "Reports retrieved successfully"));
                    
        } catch (Exception e) {
            logger.error("Failed to fetch reports by creator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to fetch reports: " + e.getMessage()));
        }
    }
    
    /**
     * Search reports by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> searchReports(
            @RequestParam String query,
            Pageable pageable) {
        
        logger.info("Searching reports with query: {}", query);
        
        try {
            Page<ReportDTO> reports = reportRepository
                    .findByNameContainingIgnoreCase(query, pageable)
                    .map(ReportDTO::summary);
            
            return ResponseEntity.ok(
                    ApiResponse.success(reports, "Search completed successfully"));
                    
        } catch (Exception e) {
            logger.error("Failed to search reports", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to search reports: " + e.getMessage()));
        }
    }
    
    /**
     * Get available report types
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> getReportTypes() {
        
        List<String> reportTypes = Arrays.stream(ReportType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(
                ApiResponse.success(reportTypes, "Report types retrieved successfully"));
    }
    
    /**
     * Delete a report
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @PathVariable UUID reportId) {
        
        logger.info("Deleting report with ID: {}", reportId);
        
        try {
            if (!reportRepository.existsById(reportId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("NOT_FOUND", "Report not found"));
            }
            
            reportRepository.deleteById(reportId);
            
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Report deleted successfully"));
                    
        } catch (Exception e) {
            logger.error("Failed to delete report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Failed to delete report: " + e.getMessage()));
        }
    }
}