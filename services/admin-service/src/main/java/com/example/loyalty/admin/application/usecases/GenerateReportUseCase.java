package com.example.loyalty.admin.application.usecases;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.repositories.ReportRepository;
import com.example.loyalty.admin.application.services.StatisticalAnalysisService;
import com.example.loyalty.admin.application.dto.ReportDTO;
import com.example.loyalty.common.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Use case for generating reports with advanced statistical analysis
 * Handles the complex logic of data aggregation, analysis, and insight generation
 */
@Service
@Transactional
public class GenerateReportUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GenerateReportUseCase.class);
    
    private final ReportRepository reportRepository;
    private final StatisticalAnalysisService statisticalAnalysisService;
    
    public GenerateReportUseCase(
            ReportRepository reportRepository,
            StatisticalAnalysisService statisticalAnalysisService) {
        this.reportRepository = reportRepository;
        this.statisticalAnalysisService = statisticalAnalysisService;
    }
    
    /**
     * Execute report generation with statistical analysis
     * 
     * @param reportId The ID of the report to generate
     * @return Generated report DTO with results
     * @throws NotFoundException if report not found
     */
    public ReportDTO execute(UUID reportId) {
        logger.info("Generating report with ID: {}", reportId);
        
        // Find the report
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report not found with ID: " + reportId));
        
        try {
            // Mark report as generating
            report.markAsGenerating();
            reportRepository.save(report);
            
            // Generate report based on type
            Map<String, Object> results = generateReportResults(report);
            
            // Mark as generated with results
            report.markAsGenerated(results);
            Report savedReport = reportRepository.save(report);
            
            logger.info("Successfully generated report with ID: {}", reportId);
            return ReportDTO.from(savedReport);
            
        } catch (Exception e) {
            logger.error("Failed to generate report with ID: {}", reportId, e);
            report.markAsFailed("Report generation failed: " + e.getMessage());
            reportRepository.save(report);
            throw new RuntimeException("Failed to generate report", e);
        }
    }
    
    private Map<String, Object> generateReportResults(Report report) {
        logger.info("Generating results for report type: {}", report.getType());
        
        Map<String, Object> results = new HashMap<>();
        results.put("generatedAt", LocalDateTime.now());
        results.put("reportId", report.getId());
        results.put("reportType", report.getType());
        
        switch (report.getType()) {
            case CORRELATION_ANALYSIS:
                results.putAll(generateCorrelationAnalysisReport(report));
                break;
            case REGRESSION_ANALYSIS:
                results.putAll(generateRegressionAnalysisReport(report));
                break;
            case ANOMALY_DETECTION:
                results.putAll(generateAnomalyDetectionReport(report));
                break;
            case USER_ANALYTICS:
                results.putAll(generateUserAnalyticsReport(report));
                break;
            case CUSTOMER_SEGMENTATION:
                results.putAll(generateCustomerSegmentationReport(report));
                break;
            default:
                results.putAll(generateGenericReport(report));
        }
        
        // Generate automated insights for all reports
        Map<String, Object> insights = statisticalAnalysisService.generateAutomatedInsights(results);
        results.put("insights", insights);
        
        return results;
    }
    
    private Map<String, Object> generateCorrelationAnalysisReport(Report report) {
        logger.info("Generating correlation analysis report");
        
        // Mock data for demonstration - in real implementation, this would fetch from actual data sources
        List<Double> xValues = generateMockMetricData("points_earned", 100);
        List<Double> yValues = generateMockMetricData("customer_satisfaction", 100);
        
        Map<String, Object> correlationResults = statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues);
        
        Map<String, Object> results = new HashMap<>();
        results.put("correlation", correlationResults);
        results.put("dataSource", "loyalty_transactions");
        results.put("xVariable", "points_earned");
        results.put("yVariable", "customer_satisfaction");
        results.put("sampleSize", xValues.size());
        
        return results;
    }
    
    private Map<String, Object> generateRegressionAnalysisReport(Report report) {
        logger.info("Generating regression analysis report");
        
        // Mock data for demonstration
        List<Double> xValues = generateMockMetricData("months_active", 100);
        List<Double> yValues = generateMockMetricData("lifetime_value", 100);
        
        Map<String, Object> regressionResults = statisticalAnalysisService.performLinearRegression(xValues, yValues);
        
        Map<String, Object> results = new HashMap<>();
        results.put("regression", regressionResults);
        results.put("dataSource", "customer_lifecycle");
        results.put("independentVariable", "months_active");
        results.put("dependentVariable", "lifetime_value");
        results.put("sampleSize", xValues.size());
        
        return results;
    }
    
    private Map<String, Object> generateAnomalyDetectionReport(Report report) {
        logger.info("Generating anomaly detection report");
        
        // Mock data with some intentional anomalies
        List<Double> data = generateMockDataWithAnomalies(200);
        
        Map<String, Object> anomalyResults = statisticalAnalysisService.detectAnomalies(data, 2.5);
        
        Map<String, Object> results = new HashMap<>();
        results.put("anomalies", anomalyResults);
        results.put("dataSource", "transaction_amounts");
        results.put("metric", "daily_transaction_volume");
        results.put("detectionThreshold", 2.5);
        results.put("timeRange", "last_30_days");
        
        return results;
    }
    
    private Map<String, Object> generateUserAnalyticsReport(Report report) {
        logger.info("Generating user analytics report");
        
        Map<String, Object> results = new HashMap<>();
        
        // Mock user analytics data
        results.put("totalUsers", 15840);
        results.put("activeUsers", 12672);
        results.put("newUsersThisMonth", 1247);
        results.put("userRetentionRate", 78.5);
        results.put("averagePointsPerUser", 2845.7);
        
        // User tier distribution
        Map<String, Integer> tierDistribution = new HashMap<>();
        tierDistribution.put("BRONZE", 8950);
        tierDistribution.put("SILVER", 4230);
        tierDistribution.put("GOLD", 2100);
        tierDistribution.put("PLATINUM", 560);
        results.put("tierDistribution", tierDistribution);
        
        // User engagement metrics over time
        List<Map<String, Object>> engagementTrend = generateMockEngagementTrend();
        results.put("engagementTrend", engagementTrend);
        
        // Perform correlation analysis on engagement data
        List<Double> days = IntStream.range(0, engagementTrend.size())
                .mapToDouble(i -> (double) i)
                .boxed()
                .collect(Collectors.toList());
        List<Double> engagement = engagementTrend.stream()
                .map(e -> ((Number) e.get("engagement")).doubleValue())
                .collect(Collectors.toList());
        
        Map<String, Object> engagementCorrelation = statisticalAnalysisService
                .performCorrelationAnalysis(days, engagement);
        results.put("engagementTrendAnalysis", engagementCorrelation);
        
        return results;
    }
    
    private Map<String, Object> generateCustomerSegmentationReport(Report report) {
        logger.info("Generating customer segmentation report");
        
        Map<String, Object> results = new HashMap<>();
        
        // Mock customer segmentation data
        List<Map<String, Object>> segments = new ArrayList<>();
        
        // High Value Customers
        Map<String, Object> highValue = new HashMap<>();
        highValue.put("name", "High Value Customers");
        highValue.put("customerCount", 2100);
        highValue.put("averageSpend", 1250.50);
        highValue.put("averagePoints", 5420);
        highValue.put("retentionRate", 94.2);
        highValue.put("characteristics", Arrays.asList("High spending", "Frequent transactions", "Long tenure"));
        segments.add(highValue);
        
        // Regular Customers
        Map<String, Object> regular = new HashMap<>();
        regular.put("name", "Regular Customers");
        regular.put("customerCount", 8950);
        regular.put("averageSpend", 425.75);
        regular.put("averagePoints", 1840);
        regular.put("retentionRate", 76.8);
        regular.put("characteristics", Arrays.asList("Moderate spending", "Regular activity", "Good engagement"));
        segments.add(regular);
        
        // Occasional Customers
        Map<String, Object> occasional = new HashMap<>();
        occasional.put("name", "Occasional Customers");
        occasional.put("customerCount", 4790);
        occasional.put("averageSpend", 125.30);
        occasional.put("averagePoints", 560);
        occasional.put("retentionRate", 45.5);
        occasional.put("characteristics", Arrays.asList("Low spending", "Infrequent activity", "Price sensitive"));
        segments.add(occasional);
        
        results.put("segments", segments);
        results.put("totalSegments", segments.size());
        results.put("segmentationMethod", "RFM_Analysis");
        
        // Segment performance comparison
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("highestValue", "High Value Customers");
        comparison.put("largestSegment", "Regular Customers");
        comparison.put("growthOpportunity", "Occasional Customers");
        results.put("segmentComparison", comparison);
        
        return results;
    }
    
    private Map<String, Object> generateGenericReport(Report report) {
        logger.info("Generating generic report for type: {}", report.getType());
        
        Map<String, Object> results = new HashMap<>();
        results.put("message", "Report generated successfully");
        results.put("type", report.getType());
        results.put("status", "completed");
        
        // Add some mock summary data
        results.put("summary", Map.of(
                "totalRecords", 1000,
                "dateRange", "2024-01-01 to 2024-12-31",
                "processingTime", "2.3 seconds"
        ));
        
        return results;
    }
    
    // Helper methods for generating mock data
    
    private List<Double> generateMockMetricData(String metricName, int size) {
        Random random = new Random(metricName.hashCode()); // Deterministic for testing
        
        return IntStream.range(0, size)
                .mapToDouble(i -> {
                    double baseValue = switch (metricName) {
                        case "points_earned" -> 100 + random.nextGaussian() * 50;
                        case "customer_satisfaction" -> 7.0 + random.nextGaussian() * 1.5;
                        case "months_active" -> 1 + random.nextDouble() * 24;
                        case "lifetime_value" -> 200 + random.nextGaussian() * 300;
                        default -> random.nextGaussian() * 100;
                    };
                    return Math.max(0, baseValue); // Ensure positive values
                })
                .boxed()
                .collect(Collectors.toList());
    }
    
    private List<Double> generateMockDataWithAnomalies(int size) {
        Random random = new Random(42); // Fixed seed for consistent results
        List<Double> data = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            if (i % 20 == 0 && random.nextDouble() < 0.3) {
                // Inject anomaly
                data.add(random.nextGaussian() * 500 + 1000); // Large outlier
            } else {
                // Normal data
                data.add(random.nextGaussian() * 50 + 200);
            }
        }
        
        return data;
    }
    
    private List<Map<String, Object>> generateMockEngagementTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        Random random = new Random(123);
        
        for (int i = 0; i < 30; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("day", i + 1);
            point.put("engagement", 50 + random.nextGaussian() * 15 + Math.sin(i * 0.2) * 10);
            point.put("activeUsers", (int) (1000 + random.nextGaussian() * 200));
            trend.add(point);
        }
        
        return trend;
    }
}