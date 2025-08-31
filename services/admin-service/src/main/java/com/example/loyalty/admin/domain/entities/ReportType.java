package com.example.loyalty.admin.domain.entities;

/**
 * Enum representing different types of reports that can be generated
 */
public enum ReportType {
    // Standard loyalty program reports
    USER_ANALYTICS,
    POINT_ANALYTICS,
    REWARDS_PERFORMANCE,
    TRANSACTION_ANALYSIS,
    
    // Statistical analysis reports
    CORRELATION_ANALYSIS,
    REGRESSION_ANALYSIS,
    COHORT_ANALYSIS,
    CHURN_PREDICTION,
    
    // Business intelligence reports
    CUSTOMER_SEGMENTATION,
    LOYALTY_ROI,
    ENGAGEMENT_METRICS,
    REVENUE_ATTRIBUTION,
    
    // Operational reports
    SYSTEM_PERFORMANCE,
    AUDIT_LOG,
    COMPLIANCE_REPORT,
    ANOMALY_DETECTION,
    
    // Custom reports
    CUSTOM_QUERY,
    DASHBOARD_EXPORT
}