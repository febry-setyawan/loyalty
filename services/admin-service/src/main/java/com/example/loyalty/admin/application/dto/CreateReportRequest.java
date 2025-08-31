package com.example.loyalty.admin.application.dto;

import com.example.loyalty.admin.domain.entities.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating new reports
 */
public class CreateReportRequest {
    
    @NotBlank(message = "Report name is required")
    @Size(max = 255, message = "Report name is too long")
    private String name;
    
    @Size(max = 1000, message = "Description is too long")
    private String description;
    
    @NotNull(message = "Report type is required")
    private ReportType type;
    
    @NotNull(message = "Creator ID is required")
    private UUID createdBy;
    
    private Map<String, Object> configuration;
    private Map<String, Object> filters;
    private Map<String, Object> statisticalConfig;
    
    // Default constructor
    public CreateReportRequest() {}
    
    // Constructor with required fields
    public CreateReportRequest(String name, ReportType type, UUID createdBy) {
        this.name = name;
        this.type = type;
        this.createdBy = createdBy;
    }
    
    // Builder pattern support
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private CreateReportRequest request = new CreateReportRequest();
        
        public Builder name(String name) {
            request.name = name;
            return this;
        }
        
        public Builder description(String description) {
            request.description = description;
            return this;
        }
        
        public Builder type(ReportType type) {
            request.type = type;
            return this;
        }
        
        public Builder createdBy(UUID createdBy) {
            request.createdBy = createdBy;
            return this;
        }
        
        public Builder configuration(Map<String, Object> configuration) {
            request.configuration = configuration;
            return this;
        }
        
        public Builder filters(Map<String, Object> filters) {
            request.filters = filters;
            return this;
        }
        
        public Builder statisticalConfig(Map<String, Object> statisticalConfig) {
            request.statisticalConfig = statisticalConfig;
            return this;
        }
        
        public CreateReportRequest build() {
            return request;
        }
    }
    
    // Getters and setters
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
    
    public UUID getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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
}