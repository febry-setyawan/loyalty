package com.example.loyalty.points.domain.enums;

/**
 * Enumeration of transaction types in the point system
 */
public enum TransactionType {
    EARN("Points earned"),
    SPEND("Points spent"),
    EXPIRE("Points expired"),
    REFUND("Points refunded"),
    BONUS("Bonus points"),
    REFERRAL("Referral points");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}