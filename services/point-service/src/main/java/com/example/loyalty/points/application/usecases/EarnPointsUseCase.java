package com.example.loyalty.points.application.usecases;

import com.example.loyalty.points.application.dto.EarnPointsDTO;
import com.example.loyalty.points.application.events.PointEventPublisher;
import com.example.loyalty.points.domain.entities.PointBalance;
import com.example.loyalty.points.domain.entities.PointTransaction;
import com.example.loyalty.points.domain.enums.EarningType;
import com.example.loyalty.points.domain.enums.TransactionType;
import com.example.loyalty.points.domain.repositories.PointBalanceRepository;
import com.example.loyalty.points.domain.repositories.PointTransactionRepository;
import com.example.loyalty.points.domain.services.PointCalculationService;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.domain.valueobjects.Points;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for earning points
 */
@Service
public class EarnPointsUseCase {
    
    private final PointTransactionRepository transactionRepository;
    private final PointBalanceRepository balanceRepository;
    private final PointCalculationService calculationService;
    private final PointEventPublisher eventPublisher;

    public EarnPointsUseCase(PointTransactionRepository transactionRepository,
                           PointBalanceRepository balanceRepository,
                           PointCalculationService calculationService,
                           PointEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.balanceRepository = balanceRepository;
        this.calculationService = calculationService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EarnPointsDTO.Response execute(EarnPointsDTO.Request request) {
        // Validate request
        validateRequest(request);

        try {
            // Convert earning type
            EarningType earningType = EarningType.valueOf(request.getEarningType().toUpperCase());
            
            // Calculate points based on transaction amount and earning rules
            Money transactionAmount = new Money(request.getTransactionAmount());
            Points earnedPoints = calculationService.calculateEarnedPoints(
                transactionAmount, 
                request.getUserId(),
                earningType,
                request.getUserTier()
            );

            // Apply bonus multiplier if provided
            if (request.getBonusMultiplier() != null) {
                earnedPoints = calculationService.calculateBonusPoints(earnedPoints, request.getBonusMultiplier());
            }

            // Get or create user balance
            PointBalance balance = balanceRepository.findByUserIdOrCreate(request.getUserId());
            
            // Create point transaction
            PointTransaction transaction = new PointTransaction(
                request.getUserId(),
                TransactionType.EARN,
                earnedPoints,
                request.getEarningType(),
                request.getReferenceId(),
                request.getDescription()
            );

            // Process transaction and update balance
            transaction.process(balance.getAvailablePoints());
            balance.addPoints(earnedPoints);

            // Save entities
            PointTransaction savedTransaction = transactionRepository.save(transaction);
            PointBalance savedBalance = balanceRepository.save(balance);

            // Publish event for real-time notifications
            eventPublisher.publishPointsEarned(savedTransaction, savedBalance);

            // Return response
            EarnPointsDTO.Response response = new EarnPointsDTO.Response(
                savedTransaction.getId().toString(),
                request.getUserId(),
                earnedPoints.longValue(),
                savedBalance.getTotalPoints().longValue(),
                "Points earned successfully"
            );
            response.setEarningType(request.getEarningType());
            response.setTransactionAmount(request.getTransactionAmount());
            
            return response;
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid earning type: " + request.getEarningType());
        }
    }

    private void validateRequest(EarnPointsDTO.Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getTransactionAmount() == null || 
            request.getTransactionAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (request.getEarningType() == null || request.getEarningType().trim().isEmpty()) {
            throw new IllegalArgumentException("Earning type is required");
        }
    }
}