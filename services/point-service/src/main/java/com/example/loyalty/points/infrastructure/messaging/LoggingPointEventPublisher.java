package com.example.loyalty.points.infrastructure.messaging;

import com.example.loyalty.points.application.events.PointEventPublisher;
import com.example.loyalty.points.domain.entities.PointBalance;
import com.example.loyalty.points.domain.entities.PointTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple logging implementation of PointEventPublisher
 * TODO: Replace with actual Kafka implementation for production
 */
@Component
public class LoggingPointEventPublisher implements PointEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingPointEventPublisher.class);

    @Override
    public void publishPointsEarned(PointTransaction transaction, PointBalance balance) {
        logger.info("Points Earned Event: User {} earned {} points. New balance: {} points. Transaction: {}",
                   transaction.getUserId(),
                   transaction.getPointsAmount().longValue(),
                   balance.getTotalPoints().longValue(),
                   transaction.getId());
        
        // In a real implementation, this would publish to Kafka
        // Example event payload:
        // {
        //   "eventType": "POINTS_EARNED",
        //   "userId": "...",
        //   "transactionId": "...",
        //   "pointsEarned": 100,
        //   "newBalance": 1500,
        //   "earningSource": "PURCHASE",
        //   "timestamp": "..."
        // }
    }

    @Override
    public void publishPointsSpent(PointTransaction transaction, PointBalance balance) {
        logger.info("Points Spent Event: User {} spent {} points. New balance: {} points. Transaction: {}",
                   transaction.getUserId(),
                   transaction.getPointsAmount().longValue(),
                   balance.getTotalPoints().longValue(),
                   transaction.getId());
    }

    @Override
    public void publishPointsExpired(PointTransaction transaction, PointBalance balance) {
        logger.info("Points Expired Event: User {} lost {} points due to expiration. New balance: {} points. Transaction: {}",
                   transaction.getUserId(),
                   transaction.getPointsAmount().longValue(),
                   balance.getTotalPoints().longValue(),
                   transaction.getId());
    }

    @Override
    public void publishReferralPointsEarned(PointTransaction transaction, PointBalance balance) {
        logger.info("Referral Points Earned Event: User {} earned {} referral points. New balance: {} points. Transaction: {}",
                   transaction.getUserId(),
                   transaction.getPointsAmount().longValue(),
                   balance.getTotalPoints().longValue(),
                   transaction.getId());
    }
}