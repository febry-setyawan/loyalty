package com.example.loyalty.points.domain.repositories;

import com.example.loyalty.points.domain.entities.PointBalance;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PointBalance domain entity
 */
public interface PointBalanceRepository {
    
    PointBalance save(PointBalance balance);
    
    Optional<PointBalance> findByUserId(UUID userId);
    
    PointBalance findByUserIdOrCreate(UUID userId);
    
    void deleteByUserId(UUID userId);
}