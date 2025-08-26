package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.domain.entities.PointTransaction;
import com.example.loyalty.points.domain.enums.TransactionType;
import com.example.loyalty.points.domain.repositories.PointTransactionRepository;
import com.example.loyalty.points.domain.valueobjects.Points;
import com.example.loyalty.points.infrastructure.persistence.entities.PointTransactionEntity;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA implementation of PointTransactionRepository
 */
@Repository
public class JpaPointTransactionRepository implements PointTransactionRepository {
    
    private final SpringDataPointTransactionRepository springRepository;

    public JpaPointTransactionRepository(SpringDataPointTransactionRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public PointTransaction save(PointTransaction transaction) {
        PointTransactionEntity entity = mapToEntity(transaction);
        PointTransactionEntity saved = springRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<PointTransaction> findById(UUID id) {
        return springRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<PointTransaction> findByUserId(UUID userId) {
        return springRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PointTransaction> findByUserIdAndDateRange(UUID userId, LocalDateTime from, LocalDateTime to) {
        return springRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, from, to)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PointTransaction> findPendingTransactions() {
        return springRepository.findPendingTransactions()
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springRepository.deleteById(id);
    }

    private PointTransactionEntity mapToEntity(PointTransaction transaction) {
        PointTransactionEntity entity = new PointTransactionEntity();
        entity.setId(transaction.getId());
        entity.setUserId(transaction.getUserId());
        entity.setTransactionType(transaction.getTransactionType().name());
        entity.setPointsAmount(transaction.getPointsAmount().longValue());
        entity.setBalanceAfter(transaction.getBalanceAfter() != null ? 
                              transaction.getBalanceAfter().longValue() : null);
        entity.setSource(transaction.getSource());
        entity.setSourceId(transaction.getSourceId());
        entity.setDescription(transaction.getDescription());
        entity.setMetadata(transaction.getMetadata());
        entity.setExpiryDate(transaction.getExpiryDate());
        entity.setCreatedAt(transaction.getCreatedAt());
        entity.setProcessedAt(transaction.getProcessedAt());
        entity.setStatus(transaction.getStatus());
        return entity;
    }

    private PointTransaction mapToDomain(PointTransactionEntity entity) {
        // Create new domain entity using reflection-like approach
        PointTransaction transaction = new PointTransaction(
            entity.getUserId(),
            TransactionType.valueOf(entity.getTransactionType()),
            new Points(entity.getPointsAmount()),
            entity.getSource(),
            entity.getSourceId(),
            entity.getDescription()
        );
        
        // Use package-private setters to set additional fields
        transaction.setId(entity.getId());
        transaction.setBalanceAfter(entity.getBalanceAfter() != null ? 
                                  new Points(entity.getBalanceAfter()) : null);
        transaction.setMetadata(entity.getMetadata());
        transaction.setExpiryDate(entity.getExpiryDate());
        transaction.setProcessedAt(entity.getProcessedAt());
        transaction.setStatus(entity.getStatus());
        
        return transaction;
    }
}