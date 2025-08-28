package com.example.loyalty.points.application.usecases;

import com.example.loyalty.points.application.dto.PointBalanceDTO;
import com.example.loyalty.points.domain.entities.PointBalance;
import com.example.loyalty.points.domain.repositories.PointBalanceRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Use case for getting point balance
 */
@Service
public class GetBalanceUseCase {
    
    private final PointBalanceRepository balanceRepository;

    public GetBalanceUseCase(PointBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public PointBalanceDTO.Response execute(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        PointBalance balance = balanceRepository.findByUserIdOrCreate(userId);

        return new PointBalanceDTO.Response(
            balance.getUserId(),
            balance.getTotalPoints().longValue(),
            balance.getAvailablePoints().longValue(),
            balance.getPendingPoints().longValue(),
            balance.getLifetimeEarned().longValue(),
            balance.getLifetimeSpent().longValue(),
            balance.getUpdatedAt()
        );
    }
}