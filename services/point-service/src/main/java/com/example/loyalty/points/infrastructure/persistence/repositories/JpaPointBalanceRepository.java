package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.domain.entities.PointBalance;
import com.example.loyalty.points.domain.repositories.PointBalanceRepository;
import com.example.loyalty.points.domain.valueobjects.Points;
import com.example.loyalty.points.infrastructure.persistence.entities.PointBalanceEntity;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of PointBalanceRepository
 */
@Repository
public class JpaPointBalanceRepository implements PointBalanceRepository {
    
    private final SpringDataPointBalanceRepository springRepository;

    public JpaPointBalanceRepository(SpringDataPointBalanceRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public PointBalance save(PointBalance balance) {
        PointBalanceEntity entity = mapToEntity(balance);
        PointBalanceEntity saved = springRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<PointBalance> findByUserId(UUID userId) {
        return springRepository.findByUserId(userId).map(this::mapToDomain);
    }

    @Override
    public PointBalance findByUserIdOrCreate(UUID userId) {
        Optional<PointBalance> existing = findByUserId(userId);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create new balance for user
        PointBalance newBalance = new PointBalance(userId);
        return save(newBalance);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springRepository.deleteByUserId(userId);
    }

    private PointBalanceEntity mapToEntity(PointBalance balance) {
        PointBalanceEntity entity = new PointBalanceEntity();
        entity.setId(balance.getId());
        entity.setUserId(balance.getUserId());
        entity.setTotalPoints(balance.getTotalPoints().longValue());
        entity.setAvailablePoints(balance.getAvailablePoints().longValue());
        entity.setPendingPoints(balance.getPendingPoints().longValue());
        entity.setLifetimeEarned(balance.getLifetimeEarned().longValue());
        entity.setLifetimeSpent(balance.getLifetimeSpent().longValue());
        entity.setCreatedAt(balance.getCreatedAt());
        entity.setUpdatedAt(balance.getUpdatedAt());
        entity.setVersion(balance.getVersion());
        return entity;
    }

    private PointBalance mapToDomain(PointBalanceEntity entity) {
        // Create new domain entity
        PointBalance balance = new PointBalance(entity.getUserId());
        
        // Use package-private setters to set additional fields
        balance.setId(entity.getId());
        balance.setTotalPoints(new Points(entity.getTotalPoints()));
        balance.setAvailablePoints(new Points(entity.getAvailablePoints()));
        balance.setPendingPoints(new Points(entity.getPendingPoints()));
        balance.setLifetimeEarned(new Points(entity.getLifetimeEarned()));
        balance.setLifetimeSpent(new Points(entity.getLifetimeSpent()));
        balance.setCreatedAt(entity.getCreatedAt());
        balance.setUpdatedAt(entity.getUpdatedAt());
        balance.setVersion(entity.getVersion());
        
        return balance;
    }
}