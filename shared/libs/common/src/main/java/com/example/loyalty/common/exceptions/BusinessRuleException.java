package com.example.loyalty.common.exceptions;

/** Exception thrown when business rules are violated */
public class BusinessRuleException extends LoyaltyException {

  public BusinessRuleException(String message) {
    super(message, "BUSINESS_RULE_ERROR", 422);
  }

  public BusinessRuleException(String message, Throwable cause) {
    super(message, "BUSINESS_RULE_ERROR", 422, cause);
  }
}
