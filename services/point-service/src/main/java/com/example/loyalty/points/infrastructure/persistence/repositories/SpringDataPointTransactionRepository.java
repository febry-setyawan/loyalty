package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.infrastructure.persistence.entities.PointTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for PointTransactionEntity
 */
@Repository
public interface SpringDataPointTransactionRepository extends JpaRepository<PointTransactionEntity, UUID> {
    
    List<PointTransactionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    List<PointTransactionEntity> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        UUID userId, LocalDateTime from, LocalDateTime to);
    
    @Query("SELECT t FROM PointTransactionEntity t WHERE t.status = 'PENDING' ORDER BY t.createdAt ASC")
    List<PointTransactionEntity> findPendingTransactions();
    
    @Query("SELECT t FROM PointTransactionEntity t WHERE t.userId = :userId AND t.transactionType = 'EARN' ORDER BY t.createdAt DESC")
    List<PointTransactionEntity> findEarnTransactionsByUserId(@Param("userId") UUID userId);
}