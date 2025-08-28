package com.example.loyalty.points.domain.services;

import com.example.loyalty.points.domain.entities.EarningRule;
import com.example.loyalty.points.domain.enums.EarningType;
import com.example.loyalty.points.domain.repositories.EarningRuleRepository;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.domain.valueobjects.Points;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for point calculations
 */
public class PointCalculationService {
    
    private final EarningRuleRepository earningRuleRepository;

    public PointCalculationService(EarningRuleRepository earningRuleRepository) {
        this.earningRuleRepository = earningRuleRepository;
    }

    /**
     * Calculate earned points based on transaction amount and earning type
     */
    public Points calculateEarnedPoints(Money transactionAmount, UUID userId, EarningType earningType) {
        return calculateEarnedPoints(transactionAmount, userId, earningType, null);
    }

    /**
     * Calculate earned points with user tier consideration
     */
    public Points calculateEarnedPoints(Money transactionAmount, UUID userId, EarningType earningType, String userTier) {
        List<EarningRule> applicableRules = earningRuleRepository.findByRuleType(earningType);
        
        Points totalPoints = Points.zero();
        
        for (EarningRule rule : applicableRules) {
            if (rule.isApplicable()) {
                Points earnedPoints = rule.calculatePoints(transactionAmount.getAmount(), userTier);
                totalPoints = totalPoints.add(earnedPoints);
            }
        }
        
        return totalPoints;
    }

    /**
     * Calculate referral points - fixed amount as per business requirements (500 points)
     */
    public Points calculateReferralPoints() {
        // Business rule: 500 points per successful referral
        return new Points(500);
    }

    /**
     * Calculate bonus points with multiplier
     */
    public Points calculateBonusPoints(Points basePoints, BigDecimal multiplier) {
        if (multiplier == null || multiplier.compareTo(BigDecimal.ONE) <= 0) {
            return basePoints;
        }
        
        return basePoints.multiply(multiplier);
    }

    /**
     * Get current earning rules for administration
     */
    public List<EarningRule> getActiveEarningRules() {
        return earningRuleRepository.findActiveRules();
    }

    /**
     * Validate transaction amount for point earning
     */
    public boolean isValidForPointEarning(Money transactionAmount) {
        return transactionAmount != null && 
               transactionAmount.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Get default purchase earning rule (1 point = Rp 1,000)
     */
    public Points calculatePurchasePoints(Money transactionAmount) {
        if (!isValidForPointEarning(transactionAmount)) {
            return Points.zero();
        }
        
        // Business rule: 1 point = Rp 1,000 transaction
        BigDecimal pointsEarned = transactionAmount.getAmount()
            .divide(BigDecimal.valueOf(1000), java.math.RoundingMode.DOWN);
        
        return new Points(pointsEarned);
    }
}