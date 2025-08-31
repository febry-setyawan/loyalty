package com.example.loyalty.admin.domain.entities;

import com.example.loyalty.common.database.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Report domain entity representing analytical reports
 * Supports custom report building with advanced filtering and statistical analysis
 */
@Entity
@Table(name = "reports")
public class Report extends BaseEntity {

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Report name is required")
    @Size(max = 255, message = "Report name too long")
    private String name;

    @Column(name = "description")
    @Size(max = 1000, message = "Description too long")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.DRAFT;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "configuration", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> configuration;

    @Column(name = "filters", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> filters;

    @Column(name = "statistical_config", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> statisticalConfig;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "results", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> results;

    @Column(name = "shared_with", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> sharedWith;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "schedule_config", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> scheduleConfig;

    // Default constructor for JPA
    protected Report() {}

    // Constructor for creating new reports
    public Report(String name, ReportType type, UUID createdBy) {
        this.name = name;
        this.type = type;
        this.createdBy = createdBy;
        this.status = ReportStatus.DRAFT;
    }

    // Business methods
    public boolean isDraft() {
        return this.status == ReportStatus.DRAFT;
    }

    public boolean isGenerated() {
        return this.status == ReportStatus.GENERATED;
    }

    public boolean isShared() {
        return this.sharedWith != null && !this.sharedWith.isEmpty();
    }

    public void markAsGenerating() {
        this.status = ReportStatus.GENERATING;
    }

    public void markAsGenerated(Map<String, Object> results) {
        this.status = ReportStatus.GENERATED;
        this.results = results;
        this.generatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = ReportStatus.FAILED;
        this.results = Map.of("error", errorMessage);
    }

    public void updateConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public void updateFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public void updateStatisticalConfig(Map<String, Object> statisticalConfig) {
        this.statisticalConfig = statisticalConfig;
    }

    public void shareWith(Map<String, Object> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public void makePublic() {
        this.isPublic = true;
    }

    public void makePrivate() {
        this.isPublic = false;
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

    public ReportStatus getStatus() {
        return status;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public Map<String, Object> getStatisticalConfig() {
        return statisticalConfig;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public Map<String, Object> getSharedWith() {
        return sharedWith;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public Map<String, Object> getScheduleConfig() {
        return scheduleConfig;
    }

    public void setScheduleConfig(Map<String, Object> scheduleConfig) {
        this.scheduleConfig = scheduleConfig;
    }
}