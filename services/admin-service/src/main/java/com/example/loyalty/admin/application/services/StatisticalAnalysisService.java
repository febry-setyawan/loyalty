package com.example.loyalty.admin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for performing statistical analysis on loyalty program data
 * Implements correlation analysis, regression analysis, and anomaly detection
 */
@Service
public class StatisticalAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticalAnalysisService.class);
    
    /**
     * Perform correlation analysis between two data series
     * 
     * @param xValues First data series
     * @param yValues Second data series
     * @return Map containing correlation coefficient and analysis results
     */
    public Map<String, Object> performCorrelationAnalysis(List<Double> xValues, List<Double> yValues) {
        logger.info("Performing correlation analysis on {} data points", xValues.size());
        
        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("Data series must have the same length");
        }
        
        if (xValues.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 data points for correlation analysis");
        }
        
        double correlation = calculatePearsonCorrelation(xValues, yValues);
        String strength = interpretCorrelationStrength(Math.abs(correlation));
        String direction = correlation >= 0 ? "positive" : "negative";
        
        Map<String, Object> result = new HashMap<>();
        result.put("correlationCoefficient", correlation);
        result.put("strength", strength);
        result.put("direction", direction);
        result.put("sampleSize", xValues.size());
        result.put("interpretation", generateCorrelationInterpretation(correlation, strength, direction));
        result.put("significanceLevel", calculateSignificanceLevel(correlation, xValues.size()));
        
        logger.info("Correlation analysis completed: coefficient = {}, strength = {}", correlation, strength);
        return result;
    }
    
    /**
     * Perform linear regression analysis
     * 
     * @param xValues Independent variable data
     * @param yValues Dependent variable data
     * @return Map containing regression coefficients and statistical measures
     */
    public Map<String, Object> performLinearRegression(List<Double> xValues, List<Double> yValues) {
        logger.info("Performing linear regression analysis on {} data points", xValues.size());
        
        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("Data series must have the same length");
        }
        
        if (xValues.size() < 3) {
            throw new IllegalArgumentException("Need at least 3 data points for regression analysis");
        }
        
        double xMean = calculateMean(xValues);
        double yMean = calculateMean(yValues);
        
        double numerator = 0;
        double denominator = 0;
        
        for (int i = 0; i < xValues.size(); i++) {
            double xDiff = xValues.get(i) - xMean;
            double yDiff = yValues.get(i) - yMean;
            numerator += xDiff * yDiff;
            denominator += xDiff * xDiff;
        }
        
        double slope = numerator / denominator;
        double intercept = yMean - slope * xMean;
        
        // Calculate R-squared
        double rSquared = calculateRSquared(xValues, yValues, slope, intercept);
        
        // Calculate residuals and statistics
        List<Double> residuals = calculateResiduals(xValues, yValues, slope, intercept);
        double standardError = calculateStandardError(residuals);
        
        Map<String, Object> result = new HashMap<>();
        result.put("slope", slope);
        result.put("intercept", intercept);
        result.put("rSquared", rSquared);
        result.put("standardError", standardError);
        result.put("equation", String.format("y = %.4fx + %.4f", slope, intercept));
        result.put("residuals", residuals);
        result.put("sampleSize", xValues.size());
        result.put("interpretation", generateRegressionInterpretation(slope, intercept, rSquared));
        
        logger.info("Linear regression completed: slope = {}, intercept = {}, R² = {}", slope, intercept, rSquared);
        return result;
    }
    
    /**
     * Detect anomalies in data using statistical methods
     * 
     * @param data Input data series
     * @param threshold Z-score threshold for anomaly detection (typically 2.0 or 3.0)
     * @return Map containing anomalies and statistics
     */
    public Map<String, Object> detectAnomalies(List<Double> data, double threshold) {
        logger.info("Detecting anomalies in {} data points with threshold {}", data.size(), threshold);
        
        if (data.size() < 3) {
            throw new IllegalArgumentException("Need at least 3 data points for anomaly detection");
        }
        
        double mean = calculateMean(data);
        double standardDeviation = calculateStandardDeviation(data, mean);
        
        List<Map<String, Object>> anomalies = new ArrayList<>();
        List<Integer> anomalyIndices = new ArrayList<>();
        
        for (int i = 0; i < data.size(); i++) {
            double value = data.get(i);
            double zScore = Math.abs((value - mean) / standardDeviation);
            
            if (zScore > threshold) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("index", i);
                anomaly.put("value", value);
                anomaly.put("zScore", zScore);
                anomaly.put("deviation", value - mean);
                anomaly.put("severity", interpretAnomalySeverity(zScore));
                
                anomalies.add(anomaly);
                anomalyIndices.add(i);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("anomalies", anomalies);
        result.put("anomalyCount", anomalies.size());
        result.put("anomalyPercentage", (anomalies.size() * 100.0) / data.size());
        result.put("mean", mean);
        result.put("standardDeviation", standardDeviation);
        result.put("threshold", threshold);
        result.put("dataSize", data.size());
        result.put("anomalyIndices", anomalyIndices);
        result.put("interpretation", generateAnomalyInterpretation(anomalies.size(), data.size(), threshold));
        
        logger.info("Anomaly detection completed: found {} anomalies out of {} data points", anomalies.size(), data.size());
        return result;
    }
    
    /**
     * Generate insights from statistical analysis
     * 
     * @param analysisResults Combined results from various statistical analyses
     * @return Map containing automated insights
     */
    public Map<String, Object> generateAutomatedInsights(Map<String, Object> analysisResults) {
        logger.info("Generating automated insights from analysis results");
        
        List<String> insights = new ArrayList<>();
        Map<String, Object> recommendations = new HashMap<>();
        
        // Analyze correlation insights
        if (analysisResults.containsKey("correlation")) {
            Map<String, Object> correlation = (Map<String, Object>) analysisResults.get("correlation");
            double coefficient = (Double) correlation.get("correlationCoefficient");
            String strength = (String) correlation.get("strength");
            
            if (Math.abs(coefficient) > 0.7) {
                insights.add(String.format("Strong %s correlation (%.3f) detected between variables. This suggests a significant relationship.", strength, coefficient));
                recommendations.put("correlation", "Consider leveraging this strong relationship for predictive modeling or business optimization.");
            } else if (Math.abs(coefficient) > 0.3) {
                insights.add(String.format("Moderate %s correlation (%.3f) detected. There may be some relationship worth investigating.", strength, coefficient));
            } else {
                insights.add("Weak correlation detected. Variables appear to be largely independent.");
            }
        }
        
        // Analyze regression insights
        if (analysisResults.containsKey("regression")) {
            Map<String, Object> regression = (Map<String, Object>) analysisResults.get("regression");
            double rSquared = (Double) regression.get("rSquared");
            
            if (rSquared > 0.8) {
                insights.add(String.format("Excellent model fit (R² = %.3f). The regression model explains %.1f%% of the variance.", rSquared, rSquared * 100));
                recommendations.put("regression", "This model is highly predictive and can be used for accurate forecasting.");
            } else if (rSquared > 0.6) {
                insights.add(String.format("Good model fit (R² = %.3f). The model explains %.1f%% of the variance.", rSquared, rSquared * 100));
                recommendations.put("regression", "Model shows good predictive power but could benefit from additional variables.");
            } else {
                insights.add(String.format("Poor model fit (R² = %.3f). Additional factors may need to be considered.", rSquared));
                recommendations.put("regression", "Consider adding more variables or using non-linear models.");
            }
        }
        
        // Analyze anomaly insights
        if (analysisResults.containsKey("anomalies")) {
            Map<String, Object> anomalies = (Map<String, Object>) analysisResults.get("anomalies");
            double anomalyPercentage = (Double) anomalies.get("anomalyPercentage");
            
            if (anomalyPercentage > 10) {
                insights.add(String.format("High anomaly rate detected (%.1f%%). This may indicate data quality issues or significant process changes.", anomalyPercentage));
                recommendations.put("anomalies", "Investigate the cause of high anomaly rate. Consider data validation and process review.");
            } else if (anomalyPercentage > 5) {
                insights.add(String.format("Moderate anomaly rate (%.1f%%) detected. Some unusual patterns present.", anomalyPercentage));
                recommendations.put("anomalies", "Review anomalous data points for potential business insights or data issues.");
            } else {
                insights.add(String.format("Low anomaly rate (%.1f%%) indicates stable, consistent data patterns.", anomalyPercentage));
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("insights", insights);
        result.put("recommendations", recommendations);
        result.put("confidence", calculateInsightConfidence(analysisResults));
        result.put("generatedAt", new Date());
        
        logger.info("Generated {} automated insights", insights.size());
        return result;
    }
    
    // Private helper methods
    
    private double calculatePearsonCorrelation(List<Double> x, List<Double> y) {
        double xMean = calculateMean(x);
        double yMean = calculateMean(y);
        
        double numerator = 0;
        double xSumSquares = 0;
        double ySumSquares = 0;
        
        for (int i = 0; i < x.size(); i++) {
            double xDiff = x.get(i) - xMean;
            double yDiff = y.get(i) - yMean;
            
            numerator += xDiff * yDiff;
            xSumSquares += xDiff * xDiff;
            ySumSquares += yDiff * yDiff;
        }
        
        double denominator = Math.sqrt(xSumSquares * ySumSquares);
        return denominator == 0 ? 0 : numerator / denominator;
    }
    
    private double calculateMean(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private double calculateStandardDeviation(List<Double> data, double mean) {
        double variance = data.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
    
    private double calculateRSquared(List<Double> x, List<Double> y, double slope, double intercept) {
        double yMean = calculateMean(y);
        
        double totalSumSquares = 0;
        double residualSumSquares = 0;
        
        for (int i = 0; i < x.size(); i++) {
            double predicted = slope * x.get(i) + intercept;
            double actual = y.get(i);
            
            totalSumSquares += Math.pow(actual - yMean, 2);
            residualSumSquares += Math.pow(actual - predicted, 2);
        }
        
        return 1 - (residualSumSquares / totalSumSquares);
    }
    
    private List<Double> calculateResiduals(List<Double> x, List<Double> y, double slope, double intercept) {
        return x.stream()
                .map(xi -> {
                    int index = x.indexOf(xi);
                    double predicted = slope * xi + intercept;
                    return y.get(index) - predicted;
                })
                .collect(Collectors.toList());
    }
    
    private double calculateStandardError(List<Double> residuals) {
        double sumSquaredResiduals = residuals.stream()
                .mapToDouble(r -> r * r)
                .sum();
        return Math.sqrt(sumSquaredResiduals / (residuals.size() - 2));
    }
    
    private String interpretCorrelationStrength(double absCorrelation) {
        if (absCorrelation >= 0.8) return "very strong";
        if (absCorrelation >= 0.6) return "strong";
        if (absCorrelation >= 0.4) return "moderate";
        if (absCorrelation >= 0.2) return "weak";
        return "very weak";
    }
    
    private String interpretAnomalySeverity(double zScore) {
        if (zScore > 3.0) return "severe";
        if (zScore > 2.5) return "high";
        if (zScore > 2.0) return "moderate";
        return "mild";
    }
    
    private double calculateSignificanceLevel(double correlation, int sampleSize) {
        // Simplified significance calculation
        double t = Math.abs(correlation) * Math.sqrt((sampleSize - 2) / (1 - correlation * correlation));
        // Return approximate p-value based on t-statistic
        return Math.max(0.001, Math.min(0.999, 1 / (1 + t)));
    }
    
    private String generateCorrelationInterpretation(double correlation, String strength, String direction) {
        return String.format("The %s %s correlation (r = %.3f) indicates that the variables %s together in a %s manner.",
                strength, direction, correlation,
                Math.abs(correlation) > 0.3 ? "tend to move" : "have little tendency to move",
                direction.equals("positive") ? "predictable" : "inversely predictable");
    }
    
    private String generateRegressionInterpretation(double slope, double intercept, double rSquared) {
        return String.format("For every unit increase in X, Y increases by %.4f units on average. " +
                "The model explains %.1f%% of the variance in the data. " +
                "The baseline value (when X=0) is %.4f.",
                slope, rSquared * 100, intercept);
    }
    
    private String generateAnomalyInterpretation(int anomalyCount, int totalSize, double threshold) {
        double percentage = (anomalyCount * 100.0) / totalSize;
        String level = percentage > 10 ? "high" : percentage > 5 ? "moderate" : "low";
        
        return String.format("Detected %d anomalies out of %d data points (%.1f%%). " +
                "This represents a %s level of anomalous activity using a %.1f standard deviation threshold.",
                anomalyCount, totalSize, percentage, level, threshold);
    }
    
    private double calculateInsightConfidence(Map<String, Object> analysisResults) {
        // Simple confidence calculation based on sample size and statistical significance
        double confidence = 0.7; // Base confidence
        
        if (analysisResults.containsKey("correlation")) {
            Map<String, Object> correlation = (Map<String, Object>) analysisResults.get("correlation");
            int sampleSize = (Integer) correlation.get("sampleSize");
            double coefficient = Math.abs((Double) correlation.get("correlationCoefficient"));
            
            // Higher sample size and stronger correlation increase confidence
            confidence += Math.min(0.2, sampleSize / 1000.0); // Sample size boost
            confidence += Math.min(0.1, coefficient); // Correlation strength boost
        }
        
        return Math.min(0.99, confidence);
    }
}