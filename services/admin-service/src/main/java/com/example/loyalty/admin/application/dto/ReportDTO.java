package com.example.loyalty.admin.application.dto;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.entities.ReportStatus;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Report entity
 * Used to transfer report data between layers
 */
public class ReportDTO {
    
    private UUID id;
    private String name;
    private String description;
    private ReportType type;
    private ReportStatus status;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime generatedAt;
    private Map<String, Object> configuration;
    private Map<String, Object> filters;
    private Map<String, Object> statisticalConfig;
    private Map<String, Object> results;
    private Map<String, Object> sharedWith;
    private Boolean isPublic;
    private Map<String, Object> scheduleConfig;
    
    // Default constructor
    public ReportDTO() {}
    
    // Factory method to create DTO from entity
    public static ReportDTO from(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.id = report.getId();
        dto.name = report.getName();
        dto.description = report.getDescription();
        dto.type = report.getType();
        dto.status = report.getStatus();
        dto.createdBy = report.getCreatedBy();
        dto.createdAt = report.getCreatedAt();
        dto.updatedAt = report.getUpdatedAt();
        dto.generatedAt = report.getGeneratedAt();
        dto.configuration = report.getConfiguration();
        dto.filters = report.getFilters();
        dto.statisticalConfig = report.getStatisticalConfig();
        dto.results = report.getResults();
        dto.sharedWith = report.getSharedWith();
        dto.isPublic = report.getIsPublic();
        dto.scheduleConfig = report.getScheduleConfig();
        return dto;
    }
    
    // Summary version without heavy data
    public static ReportDTO summary(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.id = report.getId();
        dto.name = report.getName();
        dto.description = report.getDescription();
        dto.type = report.getType();
        dto.status = report.getStatus();
        dto.createdBy = report.getCreatedBy();
        dto.createdAt = report.getCreatedAt();
        dto.updatedAt = report.getUpdatedAt();
        dto.generatedAt = report.getGeneratedAt();
        dto.isPublic = report.getIsPublic();
        // Exclude heavy data like results, configuration for summary
        return dto;
    }
    
    // Getters and setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ReportType getType() {
        return type;
    }
    
    public void setType(ReportType type) {
        this.type = type;
    }
    
    public ReportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public Map<String, Object> getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }
    
    public Map<String, Object> getFilters() {
        return filters;
    }
    
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
    
    public Map<String, Object> getStatisticalConfig() {
        return statisticalConfig;
    }
    
    public void setStatisticalConfig(Map<String, Object> statisticalConfig) {
        this.statisticalConfig = statisticalConfig;
    }
    
    public Map<String, Object> getResults() {
        return results;
    }
    
    public void setResults(Map<String, Object> results) {
        this.results = results;
    }
    
    public Map<String, Object> getSharedWith() {
        return sharedWith;
    }
    
    public void setSharedWith(Map<String, Object> sharedWith) {
        this.sharedWith = sharedWith;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Map<String, Object> getScheduleConfig() {
        return scheduleConfig;
    }
    
    public void setScheduleConfig(Map<String, Object> scheduleConfig) {
        this.scheduleConfig = scheduleConfig;
    }
}