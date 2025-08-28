package com.example.loyalty.points.application.events;

import com.example.loyalty.points.domain.entities.PointBalance;
import com.example.loyalty.points.domain.entities.PointTransaction;

/**
 * Interface for publishing point-related events
 */
public interface PointEventPublisher {
    
    /**
     * Publish event when points are earned
     */
    void publishPointsEarned(PointTransaction transaction, PointBalance balance);
    
    /**
     * Publish event when points are spent
     */
    void publishPointsSpent(PointTransaction transaction, PointBalance balance);
    
    /**
     * Publish event when points expire
     */
    void publishPointsExpired(PointTransaction transaction, PointBalance balance);
    
    /**
     * Publish event when referral points are awarded
     */
    void publishReferralPointsEarned(PointTransaction transaction, PointBalance balance);
}