package com.example.loyalty.admin.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StatisticalAnalysisService
 * Tests correlation analysis, regression analysis, and anomaly detection
 */
class StatisticalAnalysisServiceTest {

    private StatisticalAnalysisService statisticalAnalysisService;

    @BeforeEach
    void setUp() {
        statisticalAnalysisService = new StatisticalAnalysisService();
    }

    @Test
    @DisplayName("Should perform correlation analysis correctly for positive correlation")
    void should_perform_correlation_analysis_correctly_for_positive_correlation() {
        // Given - perfect positive correlation
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> yValues = Arrays.asList(2.0, 4.0, 6.0, 8.0, 10.0);

        // When
        Map<String, Object> result = statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues);

        // Then
        assertNotNull(result);
        assertEquals(1.0, (Double) result.get("correlationCoefficient"), 0.001);
        assertEquals("positive", result.get("direction"));
        assertEquals("very strong", result.get("strength"));
        assertEquals(5, result.get("sampleSize"));
        assertTrue(result.containsKey("interpretation"));
        assertTrue(result.containsKey("significanceLevel"));
    }

    @Test
    @DisplayName("Should perform correlation analysis correctly for negative correlation")
    void should_perform_correlation_analysis_correctly_for_negative_correlation() {
        // Given - perfect negative correlation
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> yValues = Arrays.asList(10.0, 8.0, 6.0, 4.0, 2.0);

        // When
        Map<String, Object> result = statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues);

        // Then
        assertNotNull(result);
        assertEquals(-1.0, (Double) result.get("correlationCoefficient"), 0.001);
        assertEquals("negative", result.get("direction"));
        assertEquals("very strong", result.get("strength"));
    }

    @Test
    @DisplayName("Should throw exception for mismatched data series lengths")
    void should_throw_exception_for_mismatched_data_series_lengths() {
        // Given
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> yValues = Arrays.asList(1.0, 2.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues)
        );

        assertEquals("Data series must have the same length", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for insufficient data points in correlation")
    void should_throw_exception_for_insufficient_data_points_in_correlation() {
        // Given
        List<Double> xValues = Arrays.asList(1.0);
        List<Double> yValues = Arrays.asList(2.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues)
        );

        assertEquals("Need at least 2 data points for correlation analysis", exception.getMessage());
    }

    @Test
    @DisplayName("Should perform linear regression correctly")
    void should_perform_linear_regression_correctly() {
        // Given - simple linear relationship: y = 2x + 1
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> yValues = Arrays.asList(3.0, 5.0, 7.0, 9.0, 11.0);

        // When
        Map<String, Object> result = statisticalAnalysisService.performLinearRegression(xValues, yValues);

        // Then
        assertNotNull(result);
        assertEquals(2.0, (Double) result.get("slope"), 0.001);
        assertEquals(1.0, (Double) result.get("intercept"), 0.001);
        assertEquals(1.0, (Double) result.get("rSquared"), 0.001);
        assertEquals("y = 2.0000x + 1.0000", result.get("equation"));
        assertNotNull(result.get("residuals"));
        assertTrue(result.containsKey("standardError"));
        assertTrue(result.containsKey("interpretation"));
    }

    @Test
    @DisplayName("Should throw exception for insufficient data points in regression")
    void should_throw_exception_for_insufficient_data_points_in_regression() {
        // Given
        List<Double> xValues = Arrays.asList(1.0, 2.0);
        List<Double> yValues = Arrays.asList(3.0, 4.0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> statisticalAnalysisService.performLinearRegression(xValues, yValues)
        );

        assertEquals("Need at least 3 data points for regression analysis", exception.getMessage());
    }

    @Test
    @DisplayName("Should detect anomalies correctly")
    void should_detect_anomalies_correctly() {
        // Given - normal data with one clear outlier
        List<Double> data = Arrays.asList(
                10.0, 12.0, 11.0, 13.0, 9.0,  // Normal data around 10-13
                100.0,  // Clear outlier
                10.5, 11.5, 12.5, 9.5
        );
        double threshold = 2.0;

        // When
        Map<String, Object> result = statisticalAnalysisService.detectAnomalies(data, threshold);

        // Then
        assertNotNull(result);
        assertTrue((Integer) result.get("anomalyCount") > 0);
        assertTrue((Double) result.get("anomalyPercentage") > 0);
        assertNotNull(result.get("anomalies"));
        assertEquals(threshold, result.get("threshold"));
        assertEquals(data.size(), result.get("dataSize"));
        assertTrue(result.containsKey("mean"));
        assertTrue(result.containsKey("standardDeviation"));
        assertTrue(result.containsKey("interpretation"));
    }

    @Test
    @DisplayName("Should throw exception for insufficient data points in anomaly detection")
    void should_throw_exception_for_insufficient_data_points_in_anomaly_detection() {
        // Given
        List<Double> data = Arrays.asList(1.0, 2.0);
        double threshold = 2.0;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> statisticalAnalysisService.detectAnomalies(data, threshold)
        );

        assertEquals("Need at least 3 data points for anomaly detection", exception.getMessage());
    }

    @Test
    @DisplayName("Should generate automated insights from analysis results")
    void should_generate_automated_insights_from_analysis_results() {
        // Given
        Map<String, Object> analysisResults = Map.of(
                "correlation", Map.of(
                        "correlationCoefficient", 0.8,
                        "strength", "strong",
                        "sampleSize", 100
                ),
                "regression", Map.of(
                        "rSquared", 0.75,
                        "slope", 2.5,
                        "intercept", 1.2
                ),
                "anomalies", Map.of(
                        "anomalyPercentage", 3.5,
                        "anomalyCount", 7
                )
        );

        // When
        Map<String, Object> result = statisticalAnalysisService.generateAutomatedInsights(analysisResults);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("insights"));
        assertTrue(result.containsKey("recommendations"));
        assertTrue(result.containsKey("confidence"));
        assertTrue(result.containsKey("generatedAt"));

        List<String> insights = (List<String>) result.get("insights");
        assertFalse(insights.isEmpty());
        
        Map<String, Object> recommendations = (Map<String, Object>) result.get("recommendations");
        assertNotNull(recommendations);
        
        Double confidence = (Double) result.get("confidence");
        assertTrue(confidence >= 0.0 && confidence <= 1.0);
    }

    @Test
    @DisplayName("Should handle zero correlation correctly")
    void should_handle_zero_correlation_correctly() {
        // Given - data with no correlation
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> yValues = Arrays.asList(5.0, 2.0, 8.0, 1.0, 6.0);

        // When
        Map<String, Object> result = statisticalAnalysisService.performCorrelationAnalysis(xValues, yValues);

        // Then
        assertNotNull(result);
        Double correlation = (Double) result.get("correlationCoefficient");
        assertTrue(Math.abs(correlation) < 0.5); // Should be weak correlation
        assertTrue(result.containsKey("strength"));
        assertTrue(result.containsKey("direction"));
    }

    @Test
    @DisplayName("Should calculate R-squared correctly for poor fit")
    void should_calculate_r_squared_correctly_for_poor_fit() {
        // Given - data with poor linear relationship
        List<Double> xValues = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> yValues = Arrays.asList(1.0, 4.0, 2.0, 8.0, 3.0); // Random-ish values

        // When
        Map<String, Object> result = statisticalAnalysisService.performLinearRegression(xValues, yValues);

        // Then
        assertNotNull(result);
        Double rSquared = (Double) result.get("rSquared");
        assertTrue(rSquared >= 0.0 && rSquared <= 1.0);
        assertTrue(result.containsKey("slope"));
        assertTrue(result.containsKey("intercept"));
    }

    @Test
    @DisplayName("Should detect no anomalies in normal data")
    void should_detect_no_anomalies_in_normal_data() {
        // Given - perfectly normal data
        List<Double> data = Arrays.asList(
                10.0, 10.1, 9.9, 10.2, 9.8,
                10.3, 9.7, 10.1, 9.9, 10.0
        );
        double threshold = 2.0;

        // When
        Map<String, Object> result = statisticalAnalysisService.detectAnomalies(data, threshold);

        // Then
        assertNotNull(result);
        assertEquals(0, result.get("anomalyCount"));
        assertEquals(0.0, (Double) result.get("anomalyPercentage"), 0.001);
        List<Map<String, Object>> anomalies = (List<Map<String, Object>>) result.get("anomalies");
        assertTrue(anomalies.isEmpty());
    }
}