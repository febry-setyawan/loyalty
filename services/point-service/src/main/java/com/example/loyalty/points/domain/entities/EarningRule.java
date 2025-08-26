package com.example.loyalty.points.domain.entities;

import com.example.loyalty.points.domain.enums.EarningType;
import com.example.loyalty.points.domain.valueobjects.Points;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain entity representing earning rules for points
 */
public class EarningRule {
    private UUID id;
    private String name;
    private EarningType ruleType;
    private BigDecimal pointsPerUnit;
    private String unitType;
    private BigDecimal multiplier;
    private BigDecimal minAmount;
    private Points maxPoints;
    private Map<String, Object> tierRestrictions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Private constructor for entity recreation
    private EarningRule() {
    }

    public EarningRule(String name, EarningType ruleType, BigDecimal pointsPerUnit, 
                      String unitType, BigDecimal multiplier) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ruleType = ruleType;
        this.pointsPerUnit = pointsPerUnit;
        this.unitType = unitType;
        this.multiplier = multiplier != null ? multiplier : BigDecimal.ONE;
        this.active = true;
        this.startDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isApplicable() {
        LocalDateTime now = LocalDateTime.now();
        return active && 
               (startDate == null || !now.isBefore(startDate)) &&
               (endDate == null || !now.isAfter(endDate));
    }

    public Points calculatePoints(BigDecimal transactionAmount, String userTier) {
        if (!isApplicable()) {
            return Points.zero();
        }
        
        // Check minimum amount requirement
        if (minAmount != null && transactionAmount.compareTo(minAmount) < 0) {
            return Points.zero();
        }
        
        // Check tier restrictions
        if (tierRestrictions != null && userTier != null) {
            // Implementation would check if user tier is allowed
            // For now, assume all tiers are allowed if no restriction
        }
        
        // Calculate base points
        BigDecimal basePoints;
        if ("DOLLAR".equals(unitType)) {
            // 1 point per 1000 units (Rp 1,000)
            basePoints = transactionAmount.divide(BigDecimal.valueOf(1000), java.math.RoundingMode.DOWN)
                                        .multiply(pointsPerUnit);
        } else {
            basePoints = pointsPerUnit;
        }
        
        // Apply multiplier
        BigDecimal finalPoints = basePoints.multiply(multiplier);
        
        // Check maximum points limit
        if (maxPoints != null && finalPoints.compareTo(maxPoints.getValue()) > 0) {
            finalPoints = maxPoints.getValue();
        }
        
        return new Points(finalPoints);
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMultiplier(BigDecimal newMultiplier) {
        this.multiplier = newMultiplier;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public EarningType getRuleType() { return ruleType; }
    public BigDecimal getPointsPerUnit() { return pointsPerUnit; }
    public String getUnitType() { return unitType; }
    public BigDecimal getMultiplier() { return multiplier; }
    public BigDecimal getMinAmount() { return minAmount; }
    public Points getMaxPoints() { return maxPoints; }
    public Map<String, Object> getTierRestrictions() { return tierRestrictions; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Public setters for entity recreation (needed by infrastructure layer)
    public void setId(UUID id) { this.id = id; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public void setMaxPoints(Points maxPoints) { this.maxPoints = maxPoints; }
    public void setTierRestrictions(Map<String, Object> tierRestrictions) { this.tierRestrictions = tierRestrictions; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}