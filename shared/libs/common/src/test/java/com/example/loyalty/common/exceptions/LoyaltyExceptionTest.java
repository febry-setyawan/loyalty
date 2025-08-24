package com.example.loyalty.common.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoyaltyExceptionTest {

  private static class TestLoyaltyException extends LoyaltyException {
    public TestLoyaltyException(String message, String code, int statusCode) {
      super(message, code, statusCode);
    }
  }

  @Test
  void shouldCreateExceptionWithCorrectProperties() {
    // Given
    String message = "Test error message";
    String code = "TEST_ERROR";
    int statusCode = 400;

    // When
    TestLoyaltyException exception = new TestLoyaltyException(message, code, statusCode);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals(code, exception.getCode());
    assertEquals(statusCode, exception.getStatusCode());
    assertNotNull(exception.getTimestamp());
  }

  @Test
  void shouldCreateValidationException() {
    // Given
    String message = "Invalid field value";
    String field = "email";

    // When
    ValidationException exception = new ValidationException(message, field);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals("VALIDATION_ERROR", exception.getCode());
    assertEquals(400, exception.getStatusCode());
    assertEquals(field, exception.getField());
  }

  @Test
  void shouldCreateNotFoundException() {
    // Given
    String resource = "User";
    String id = "123";

    // When
    NotFoundException exception = new NotFoundException(resource, id);

    // Then
    assertEquals("User with ID 123 not found", exception.getMessage());
    assertEquals("NOT_FOUND", exception.getCode());
    assertEquals(404, exception.getStatusCode());
    assertEquals(resource, exception.getResource());
    assertEquals(id, exception.getResourceId());
  }

  @Test
  void shouldCreateBusinessRuleException() {
    // Given
    String message = "Business rule violated";

    // When
    BusinessRuleException exception = new BusinessRuleException(message);

    // Then
    assertEquals(message, exception.getMessage());
    assertEquals("BUSINESS_RULE_ERROR", exception.getCode());
    assertEquals(422, exception.getStatusCode());
  }
}
