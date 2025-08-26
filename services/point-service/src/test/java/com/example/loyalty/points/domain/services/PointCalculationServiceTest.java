package com.example.loyalty.points.domain.services;

import com.example.loyalty.points.domain.entities.EarningRule;
import com.example.loyalty.points.domain.enums.EarningType;
import com.example.loyalty.points.domain.repositories.EarningRuleRepository;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.domain.valueobjects.Points;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointCalculationServiceTest {

    @Mock
    private EarningRuleRepository earningRuleRepository;
    
    private PointCalculationService pointCalculationService;

    @BeforeEach
    void setUp() {
        pointCalculationService = new PointCalculationService(earningRuleRepository);
    }

    @Test
    void testCalculatePurchasePoints_BasicRule() {
        // Given
        Money transactionAmount = new Money(BigDecimal.valueOf(5000)); // Rp 5,000
        
        // When
        Points result = pointCalculationService.calculatePurchasePoints(transactionAmount);
        
        // Then
        assertEquals(5L, result.longValue()); // Should get 5 points (5000/1000)
    }

    @Test
    void testCalculatePurchasePoints_ZeroAmount() {
        // Given
        Money transactionAmount = new Money(BigDecimal.ZERO);
        
        // When
        Points result = pointCalculationService.calculatePurchasePoints(transactionAmount);
        
        // Then
        assertEquals(0L, result.longValue());
    }

    @Test
    void testCalculateReferralPoints() {
        // Given
        UUID referredUserId = UUID.randomUUID();
        
        // When
        Points result = pointCalculationService.calculateReferralPoints(referredUserId);
        
        // Then
        assertEquals(500L, result.longValue()); // Business rule: 500 points per referral
    }

    @Test
    void testCalculateBonusPoints_WithMultiplier() {
        // Given
        Points basePoints = new Points(100);
        BigDecimal multiplier = BigDecimal.valueOf(2.0); // 2x multiplier
        
        // When
        Points result = pointCalculationService.calculateBonusPoints(basePoints, multiplier);
        
        // Then
        assertEquals(200L, result.longValue()); // 100 * 2.0 = 200
    }

    @Test
    void testCalculateBonusPoints_NoMultiplier() {
        // Given
        Points basePoints = new Points(100);
        BigDecimal multiplier = BigDecimal.ONE;
        
        // When
        Points result = pointCalculationService.calculateBonusPoints(basePoints, multiplier);
        
        // Then
        assertEquals(100L, result.longValue()); // No change
    }

    @Test
    void testIsValidForPointEarning() {
        // Test valid amount
        assertTrue(pointCalculationService.isValidForPointEarning(new Money(BigDecimal.valueOf(1000))));
        
        // Test zero amount
        assertFalse(pointCalculationService.isValidForPointEarning(new Money(BigDecimal.ZERO)));
        
        // Test null amount
        assertFalse(pointCalculationService.isValidForPointEarning(null));
    }
}