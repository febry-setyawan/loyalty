package com.example.loyalty.points.domain.entities;

import com.example.loyalty.points.domain.enums.TransactionType;
import com.example.loyalty.points.domain.valueobjects.Money;
import com.example.loyalty.points.domain.valueobjects.Points;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a point transaction
 */
public class PointTransaction {
    private UUID id;
    private UUID userId;
    private TransactionType transactionType;
    private Points pointsAmount;
    private Points balanceAfter;
    private String source;
    private UUID sourceId;
    private String description;
    private String metadata;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String status;

    // Private constructor for entity recreation
    private PointTransaction() {
    }

    public PointTransaction(UUID userId, TransactionType transactionType, Points pointsAmount,
                          String source, UUID sourceId, String description) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.transactionType = transactionType;
        this.pointsAmount = pointsAmount;
        this.source = source;
        this.sourceId = sourceId;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void process(Points currentBalance) {
        if ("PROCESSED".equals(this.status)) {
            throw new IllegalStateException("Transaction is already processed");
        }
        
        this.processedAt = LocalDateTime.now();
        this.status = "PROCESSED";
        
        // Calculate balance after transaction
        if (transactionType == TransactionType.EARN || 
            transactionType == TransactionType.BONUS || 
            transactionType == TransactionType.REFERRAL ||
            transactionType == TransactionType.REFUND) {
            this.balanceAfter = currentBalance.add(pointsAmount);
        } else if (transactionType == TransactionType.SPEND || 
                   transactionType == TransactionType.EXPIRE) {
            this.balanceAfter = currentBalance.subtract(pointsAmount);
        }
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public void fail(String reason) {
        this.status = "FAILED";
        this.metadata = reason;
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isProcessed() {
        return "PROCESSED".equals(status);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public TransactionType getTransactionType() { return transactionType; }
    public Points getPointsAmount() { return pointsAmount; }
    public Points getBalanceAfter() { return balanceAfter; }
    public String getSource() { return source; }
    public UUID getSourceId() { return sourceId; }
    public String getDescription() { return description; }
    public String getMetadata() { return metadata; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getStatus() { return status; }

    // Setters for entity recreation (needed by infrastructure layer)
    void setId(UUID id) { this.id = id; }
    void setBalanceAfter(Points balanceAfter) { this.balanceAfter = balanceAfter; }
    void setMetadata(String metadata) { this.metadata = metadata; }
    void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    void setStatus(String status) { this.status = status; }
}