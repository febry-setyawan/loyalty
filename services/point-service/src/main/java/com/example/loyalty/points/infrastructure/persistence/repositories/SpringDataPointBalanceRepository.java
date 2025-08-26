package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.infrastructure.persistence.entities.PointBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for PointBalanceEntity
 */
@Repository
public interface SpringDataPointBalanceRepository extends JpaRepository<PointBalanceEntity, UUID> {
    
    Optional<PointBalanceEntity> findByUserId(UUID userId);
    
    void deleteByUserId(UUID userId);
}