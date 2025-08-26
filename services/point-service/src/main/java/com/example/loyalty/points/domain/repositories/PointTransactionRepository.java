package com.example.loyalty.points.domain.repositories;

import com.example.loyalty.points.domain.entities.PointTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PointTransaction domain entity
 */
public interface PointTransactionRepository {
    
    PointTransaction save(PointTransaction transaction);
    
    Optional<PointTransaction> findById(UUID id);
    
    List<PointTransaction> findByUserId(UUID userId);
    
    List<PointTransaction> findByUserIdAndDateRange(UUID userId, LocalDateTime from, LocalDateTime to);
    
    List<PointTransaction> findPendingTransactions();
    
    void deleteById(UUID id);
}