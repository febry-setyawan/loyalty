package com.example.loyalty.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark sensitive data fields for encryption and auditing Used for PII data protection
 * and compliance
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveData {

  /** Data classification level */
  DataClassification classification() default DataClassification.CONFIDENTIAL;

  /** Reason for sensitivity (for documentation) */
  String reason() default "";

  /** Data classification levels based on sensitivity */
  enum DataClassification {
    PUBLIC, // No protection needed
    INTERNAL, // Company internal only
    CONFIDENTIAL, // Requires authorization
    RESTRICTED // Highest protection level (PII, financial data)
  }
}
