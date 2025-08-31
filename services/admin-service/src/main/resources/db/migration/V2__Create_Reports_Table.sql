-- V2: Create reports table for advanced reporting capabilities
-- This supports the admin service's advanced reporting and analytics features

CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL CHECK (type IN (
        'USER_ANALYTICS',
        'POINT_ANALYTICS', 
        'REWARDS_PERFORMANCE',
        'TRANSACTION_ANALYSIS',
        'CORRELATION_ANALYSIS',
        'REGRESSION_ANALYSIS',
        'COHORT_ANALYSIS',
        'CHURN_PREDICTION',
        'CUSTOMER_SEGMENTATION',
        'LOYALTY_ROI',
        'ENGAGEMENT_METRICS',
        'REVENUE_ATTRIBUTION',
        'SYSTEM_PERFORMANCE',
        'AUDIT_LOG',
        'COMPLIANCE_REPORT',
        'ANOMALY_DETECTION',
        'CUSTOM_QUERY',
        'DASHBOARD_EXPORT'
    )),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN (
        'DRAFT',
        'GENERATING', 
        'GENERATED',
        'FAILED',
        'SCHEDULED',
        'ARCHIVED'
    )),
    created_by UUID NOT NULL,
    configuration JSONB,
    filters JSONB,
    statistical_config JSONB,
    generated_at TIMESTAMP,
    results JSONB,
    shared_with JSONB,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    schedule_config JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_reports_created_by ON reports(created_by);
CREATE INDEX idx_reports_type ON reports(type);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_name_gin ON reports USING gin (to_tsvector('english', name));
CREATE INDEX idx_reports_is_public ON reports(is_public) WHERE is_public = true;
CREATE INDEX idx_reports_generated_at ON reports(generated_at) WHERE generated_at IS NOT NULL;

-- Create GIN indexes for JSONB columns for efficient JSON queries
CREATE INDEX idx_reports_shared_with_gin ON reports USING gin (shared_with);
CREATE INDEX idx_reports_configuration_gin ON reports USING gin (configuration);
CREATE INDEX idx_reports_results_gin ON reports USING gin (results);

-- Create trigger to automatically update updated_at timestamp for reports
CREATE TRIGGER update_reports_updated_at
    BEFORE UPDATE ON reports
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments to document the table
COMMENT ON TABLE reports IS 'Advanced reporting system for loyalty program analytics';
COMMENT ON COLUMN reports.configuration IS 'JSON configuration for report parameters and settings';
COMMENT ON COLUMN reports.filters IS 'JSON filters applied to the report data';
COMMENT ON COLUMN reports.statistical_config IS 'Configuration for statistical analysis (correlation, regression, etc.)';
COMMENT ON COLUMN reports.results IS 'Generated report results including analysis and insights';
COMMENT ON COLUMN reports.shared_with IS 'JSON object defining who the report is shared with';
COMMENT ON COLUMN reports.schedule_config IS 'Configuration for automated report scheduling';