package com.example.loyalty.points.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for points_balance table
 */
@Entity
@Table(name = "points_balance")
public class PointBalanceEntity {
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    
    @Column(name = "total_points", nullable = false)
    private Long totalPoints = 0L;
    
    @Column(name = "available_points", nullable = false)
    private Long availablePoints = 0L;
    
    @Column(name = "pending_points", nullable = false)
    private Long pendingPoints = 0L;
    
    @Column(name = "lifetime_earned", nullable = false)
    private Long lifetimeEarned = 0L;
    
    @Column(name = "lifetime_spent", nullable = false)
    private Long lifetimeSpent = 0L;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    // Default constructor
    public PointBalanceEntity() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public Long getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Long totalPoints) { this.totalPoints = totalPoints; }
    
    public Long getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(Long availablePoints) { this.availablePoints = availablePoints; }
    
    public Long getPendingPoints() { return pendingPoints; }
    public void setPendingPoints(Long pendingPoints) { this.pendingPoints = pendingPoints; }
    
    public Long getLifetimeEarned() { return lifetimeEarned; }
    public void setLifetimeEarned(Long lifetimeEarned) { this.lifetimeEarned = lifetimeEarned; }
    
    public Long getLifetimeSpent() { return lifetimeSpent; }
    public void setLifetimeSpent(Long lifetimeSpent) { this.lifetimeSpent = lifetimeSpent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}