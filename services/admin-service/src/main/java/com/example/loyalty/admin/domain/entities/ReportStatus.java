package com.example.loyalty.admin.domain.entities;

/**
 * Enum representing the current status of a report
 */
public enum ReportStatus {
    DRAFT,          // Report is being configured
    GENERATING,     // Report generation is in progress
    GENERATED,      // Report has been successfully generated
    FAILED,         // Report generation failed
    SCHEDULED,      // Report is scheduled for future generation
    ARCHIVED        // Report has been archived
}