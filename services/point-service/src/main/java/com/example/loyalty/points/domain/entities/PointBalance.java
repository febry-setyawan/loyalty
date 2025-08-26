package com.example.loyalty.points.domain.entities;

import com.example.loyalty.points.domain.valueobjects.Points;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a user's point balance
 */
public class PointBalance {
    private UUID id;
    private UUID userId;
    private Points totalPoints;
    private Points availablePoints;
    private Points pendingPoints;
    private Points lifetimeEarned;
    private Points lifetimeSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

    // Private constructor for entity recreation
    private PointBalance() {
    }

    public PointBalance(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.totalPoints = Points.zero();
        this.availablePoints = Points.zero();
        this.pendingPoints = Points.zero();
        this.lifetimeEarned = Points.zero();
        this.lifetimeSpent = Points.zero();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0;
    }

    public void addPoints(Points points) {
        this.totalPoints = this.totalPoints.add(points);
        this.availablePoints = this.availablePoints.add(points);
        this.lifetimeEarned = this.lifetimeEarned.add(points);
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    public void spendPoints(Points points) {
        if (!this.availablePoints.isGreaterThanOrEqual(points)) {
            throw new IllegalArgumentException("Insufficient available points");
        }
        
        this.totalPoints = this.totalPoints.subtract(points);
        this.availablePoints = this.availablePoints.subtract(points);
        this.lifetimeSpent = this.lifetimeSpent.add(points);
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    public void addPendingPoints(Points points) {
        this.pendingPoints = this.pendingPoints.add(points);
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    public void confirmPendingPoints(Points points) {
        if (!this.pendingPoints.isGreaterThanOrEqual(points)) {
            throw new IllegalArgumentException("Insufficient pending points to confirm");
        }
        
        this.pendingPoints = this.pendingPoints.subtract(points);
        this.totalPoints = this.totalPoints.add(points);
        this.availablePoints = this.availablePoints.add(points);
        this.lifetimeEarned = this.lifetimeEarned.add(points);
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    public void expirePoints(Points points) {
        if (!this.availablePoints.isGreaterThanOrEqual(points)) {
            throw new IllegalArgumentException("Cannot expire more points than available");
        }
        
        this.totalPoints = this.totalPoints.subtract(points);
        this.availablePoints = this.availablePoints.subtract(points);
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Points getTotalPoints() { return totalPoints; }
    public Points getAvailablePoints() { return availablePoints; }
    public Points getPendingPoints() { return pendingPoints; }
    public Points getLifetimeEarned() { return lifetimeEarned; }
    public Points getLifetimeSpent() { return lifetimeSpent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public int getVersion() { return version; }

    // Public setters for entity recreation (needed by infrastructure layer)
    public void setId(UUID id) { this.id = id; }
    public void setTotalPoints(Points totalPoints) { this.totalPoints = totalPoints; }
    public void setAvailablePoints(Points availablePoints) { this.availablePoints = availablePoints; }
    public void setPendingPoints(Points pendingPoints) { this.pendingPoints = pendingPoints; }
    public void setLifetimeEarned(Points lifetimeEarned) { this.lifetimeEarned = lifetimeEarned; }
    public void setLifetimeSpent(Points lifetimeSpent) { this.lifetimeSpent = lifetimeSpent; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setVersion(int version) { this.version = version; }
}