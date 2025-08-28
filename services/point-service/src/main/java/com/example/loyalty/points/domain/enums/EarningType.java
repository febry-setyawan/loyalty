package com.example.loyalty.points.domain.enums;

/**
 * Enumeration of earning types in the point system
 */
public enum EarningType {
    PURCHASE("Points from purchase"),
    REFERRAL("Points from referral"),
    SIGNUP("Points from signup bonus"),
    REVIEW("Points from review"),
    SOCIAL_SHARE("Points from social sharing"),
    BONUS_EVENT("Points from bonus event"),
    MANUAL("Manually awarded points");

    private final String description;

    EarningType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}