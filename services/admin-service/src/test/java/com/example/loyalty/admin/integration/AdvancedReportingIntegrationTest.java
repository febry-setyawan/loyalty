package com.example.loyalty.admin.integration;

import com.example.loyalty.admin.application.dto.CreateReportRequest;
import com.example.loyalty.admin.application.dto.ReportDTO;
import com.example.loyalty.admin.application.services.StatisticalAnalysisService;
import com.example.loyalty.admin.application.usecases.CreateReportUseCase;
import com.example.loyalty.admin.application.usecases.GenerateReportUseCase;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.entities.ReportStatus;
import com.example.loyalty.admin.domain.repositories.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the advanced reporting system
 * Tests the complete workflow from report creation to generation with statistical analysis
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdvancedReportingIntegrationTest {

    @Autowired
    private CreateReportUseCase createReportUseCase;

    @Autowired
    private GenerateReportUseCase generateReportUseCase;

    @Autowired
    private StatisticalAnalysisService statisticalAnalysisService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    @DisplayName("Complete reporting workflow: create, generate, and analyze correlation report")
    void complete_reporting_workflow_correlation_analysis() {
        // Given - Create a correlation analysis report
        UUID creatorId = UUID.randomUUID();
        
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("xVariable", "points_earned");
        configuration.put("yVariable", "customer_satisfaction");
        configuration.put("dataSource", "loyalty_transactions");
        
        Map<String, Object> filters = new HashMap<>();
        filters.put("dateRange", "last_30_days");
        filters.put("userTier", "ALL");
        
        Map<String, Object> statisticalConfig = new HashMap<>();
        statisticalConfig.put("correlationThreshold", 0.5);
        statisticalConfig.put("significanceLevel", 0.05);
        
        CreateReportRequest request = CreateReportRequest.builder()
                .name("Customer Satisfaction vs Points Earned Analysis")
                .description("Analyze correlation between points earned and customer satisfaction scores")
                .type(ReportType.CORRELATION_ANALYSIS)
                .createdBy(creatorId)
                .configuration(configuration)
                .filters(filters)
                .statisticalConfig(statisticalConfig)
                .build();

        // When - Create the report
        ReportDTO createdReport = createReportUseCase.execute(request);

        // Then - Verify report creation
        assertNotNull(createdReport);
        assertNotNull(createdReport.getId());
        assertEquals(request.getName(), createdReport.getName());
        assertEquals(request.getDescription(), createdReport.getDescription());
        assertEquals(ReportType.CORRELATION_ANALYSIS, createdReport.getType());
        assertEquals(ReportStatus.DRAFT, createdReport.getStatus());
        assertEquals(creatorId, createdReport.getCreatedBy());
        assertNotNull(createdReport.getConfiguration());
        assertNotNull(createdReport.getFilters());
        assertNotNull(createdReport.getStatisticalConfig());

        // When - Generate the report
        ReportDTO generatedReport = generateReportUseCase.execute(createdReport.getId());

        // Then - Verify report generation and results
        assertNotNull(generatedReport);
        assertEquals(ReportStatus.GENERATED, generatedReport.getStatus());
        assertNotNull(generatedReport.getGeneratedAt());
        assertNotNull(generatedReport.getResults());

        // Verify statistical analysis results
        Map<String, Object> results = generatedReport.getResults();
        assertTrue(results.containsKey("correlation"));
        assertTrue(results.containsKey("insights"));
        
        Map<String, Object> correlationResults = (Map<String, Object>) results.get("correlation");
        assertNotNull(correlationResults.get("correlationCoefficient"));
        assertNotNull(correlationResults.get("strength"));
        assertNotNull(correlationResults.get("direction"));
        assertNotNull(correlationResults.get("sampleSize"));
        assertNotNull(correlationResults.get("interpretation"));

        Map<String, Object> insights = (Map<String, Object>) results.get("insights");
        assertTrue(insights.containsKey("insights"));
        assertTrue(insights.containsKey("recommendations"));
        assertTrue(insights.containsKey("confidence"));
    }

    @Test
    @DisplayName("Complete reporting workflow: anomaly detection report")
    void complete_reporting_workflow_anomaly_detection() {
        // Given - Create an anomaly detection report
        UUID creatorId = UUID.randomUUID();
        
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("metric", "daily_transaction_volume");
        configuration.put("dataSource", "transaction_amounts");
        configuration.put("detectionThreshold", 2.5);
        
        CreateReportRequest request = CreateReportRequest.builder()
                .name("Transaction Volume Anomaly Detection")
                .description("Detect unusual patterns in daily transaction volumes")
                .type(ReportType.ANOMALY_DETECTION)
                .createdBy(creatorId)
                .configuration(configuration)
                .build();

        // When - Create and generate the report
        ReportDTO createdReport = createReportUseCase.execute(request);
        ReportDTO generatedReport = generateReportUseCase.execute(createdReport.getId());

        // Then - Verify anomaly detection results
        assertNotNull(generatedReport);
        assertEquals(ReportStatus.GENERATED, generatedReport.getStatus());
        
        Map<String, Object> results = generatedReport.getResults();
        assertTrue(results.containsKey("anomalies"));
        
        Map<String, Object> anomalyResults = (Map<String, Object>) results.get("anomalies");
        assertNotNull(anomalyResults.get("anomalyCount"));
        assertNotNull(anomalyResults.get("anomalyPercentage"));
        assertNotNull(anomalyResults.get("mean"));
        assertNotNull(anomalyResults.get("standardDeviation"));
        assertNotNull(anomalyResults.get("interpretation"));
        assertTrue(anomalyResults.containsKey("anomalies")); // List of detected anomalies
    }

    @Test
    @DisplayName("Statistical analysis service integration test")
    void statistical_analysis_service_integration_test() {
        // Given - Sample data for various analyses
        List<Double> xValues = List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
        List<Double> yValues = List.of(2.1, 4.2, 5.9, 8.1, 9.8, 12.2, 14.1, 15.9, 18.2, 20.1);
        List<Double> dataWithAnomalies = List.of(10.0, 12.0, 11.0, 13.0, 9.0, 100.0, 10.5, 11.5, 12.5, 9.5);

        // When - Perform correlation analysis
        Map<String, Object> correlationResults = statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues);

        // Then - Verify correlation analysis
        assertNotNull(correlationResults);
        Double correlation = (Double) correlationResults.get("correlationCoefficient");
        assertTrue(correlation > 0.9); // Should be very strong positive correlation
        assertEquals("positive", correlationResults.get("direction"));
        assertEquals("very strong", correlationResults.get("strength"));

        // When - Perform regression analysis
        Map<String, Object> regressionResults = statisticalAnalysisService.performLinearRegression(xValues, yValues);

        // Then - Verify regression analysis
        assertNotNull(regressionResults);
        Double slope = (Double) regressionResults.get("slope");
        Double intercept = (Double) regressionResults.get("intercept");
        Double rSquared = (Double) regressionResults.get("rSquared");
        assertTrue(slope > 0); // Positive relationship
        assertTrue(rSquared > 0.9); // Strong fit
        assertNotNull(regressionResults.get("equation"));
        assertNotNull(regressionResults.get("interpretation"));

        // When - Perform anomaly detection
        Map<String, Object> anomalyResults = statisticalAnalysisService.detectAnomalies(dataWithAnomalies, 2.0);

        // Then - Verify anomaly detection
        assertNotNull(anomalyResults);
        Integer anomalyCount = (Integer) anomalyResults.get("anomalyCount");
        assertTrue(anomalyCount > 0); // Should detect the outlier (100.0)
        List<Map<String, Object>> anomalies = (List<Map<String, Object>>) anomalyResults.get("anomalies");
        assertFalse(anomalies.isEmpty());

        // When - Generate automated insights
        Map<String, Object> allResults = Map.of(
                "correlation", correlationResults,
                "regression", regressionResults,
                "anomalies", anomalyResults
        );
        Map<String, Object> insights = statisticalAnalysisService.generateAutomatedInsights(allResults);

        // Then - Verify insights generation
        assertNotNull(insights);
        List<String> insightsList = (List<String>) insights.get("insights");
        Map<String, Object> recommendations = (Map<String, Object>) insights.get("recommendations");
        Double confidence = (Double) insights.get("confidence");
        
        assertFalse(insightsList.isEmpty());
        assertNotNull(recommendations);
        assertTrue(confidence >= 0.0 && confidence <= 1.0);
    }

    @Test
    @DisplayName("Customer segmentation report workflow")
    void customer_segmentation_report_workflow() {
        // Given - Create customer segmentation report
        UUID creatorId = UUID.randomUUID();
        
        CreateReportRequest request = CreateReportRequest.builder()
                .name("Customer Segmentation Analysis")
                .description("Analyze customer segments based on behavior patterns")
                .type(ReportType.CUSTOMER_SEGMENTATION)
                .createdBy(creatorId)
                .build();

        // When - Create and generate the report
        ReportDTO createdReport = createReportUseCase.execute(request);
        ReportDTO generatedReport = generateReportUseCase.execute(createdReport.getId());

        // Then - Verify segmentation results
        assertNotNull(generatedReport);
        assertEquals(ReportStatus.GENERATED, generatedReport.getStatus());
        
        Map<String, Object> results = generatedReport.getResults();
        assertTrue(results.containsKey("segments"));
        assertTrue(results.containsKey("totalSegments"));
        assertTrue(results.containsKey("segmentComparison"));
        
        List<Map<String, Object>> segments = (List<Map<String, Object>>) results.get("segments");
        assertFalse(segments.isEmpty());
        
        // Verify each segment has required information
        for (Map<String, Object> segment : segments) {
            assertTrue(segment.containsKey("name"));
            assertTrue(segment.containsKey("customerCount"));
            assertTrue(segment.containsKey("averageSpend"));
            assertTrue(segment.containsKey("averagePoints"));
            assertTrue(segment.containsKey("retentionRate"));
            assertTrue(segment.containsKey("characteristics"));
        }
    }
}