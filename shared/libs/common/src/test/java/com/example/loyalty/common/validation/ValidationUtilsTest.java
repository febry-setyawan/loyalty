package com.example.loyalty.common.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.example.loyalty.common.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationUtilsTest {

  @Test
  void shouldValidateValidEmail() {
    assertTrue(ValidationUtils.isValidEmail("test@example.com"));
    assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
    assertTrue(ValidationUtils.isValidEmail("user+tag@example.org"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid", "@example.com", "test@", "test.example.com", ""})
  void shouldRejectInvalidEmails(String email) {
    assertFalse(ValidationUtils.isValidEmail(email));
  }

  @Test
  void shouldValidateValidPhone() {
    assertTrue(ValidationUtils.isValidPhone("+1234567890"));
    assertTrue(ValidationUtils.isValidPhone("+628123456789"));
    assertTrue(ValidationUtils.isValidPhone("+447123456789"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1234567890", "+", "+1", "123", "abc", ""})
  void shouldRejectInvalidPhones(String phone) {
    assertFalse(ValidationUtils.isValidPhone(phone));
  }

  @Test
  void shouldValidateValidPassword() {
    assertTrue(ValidationUtils.isValidPassword("ValidPass123!"));
    assertTrue(ValidationUtils.isValidPassword("SecureP@ss1"));
    assertTrue(ValidationUtils.isValidPassword("MyP@ssw0rd"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "password",
        "Password",
        "Password1",
        "PASSWORD!",
        "Pass1!",
        "verylongpasswordwithoutuppercasenumbersymbols"
      })
  void shouldRejectInvalidPasswords(String password) {
    assertFalse(ValidationUtils.isValidPassword(password));
  }

  @Test
  void shouldRequireNonNullValue() {
    String value = "test";
    assertEquals(value, ValidationUtils.requireNonNull(value, "testField"));

    assertThrows(
        ValidationException.class, () -> ValidationUtils.requireNonNull(null, "testField"));
  }

  @Test
  void shouldRequireNonEmptyString() {
    assertEquals("test", ValidationUtils.requireNonEmpty("test", "testField"));
    assertEquals("test", ValidationUtils.requireNonEmpty("  test  ", "testField"));

    assertThrows(ValidationException.class, () -> ValidationUtils.requireNonEmpty("", "testField"));
    assertThrows(
        ValidationException.class, () -> ValidationUtils.requireNonEmpty("   ", "testField"));
    assertThrows(
        ValidationException.class, () -> ValidationUtils.requireNonEmpty(null, "testField"));
  }

  @Test
  void shouldValidateStringLength() {
    assertEquals("test", ValidationUtils.requireLength("test", "testField", 1, 10));

    assertThrows(
        ValidationException.class, () -> ValidationUtils.requireLength("", "testField", 1, 10));
    assertThrows(
        ValidationException.class,
        () ->
            ValidationUtils.requireLength(
                "verylongstringthatexceedsmaxlength", "testField", 1, 10));
  }

  @Test
  void shouldRequirePositiveNumber() {
    assertEquals(5, ValidationUtils.requirePositive(5, "testField"));
    assertEquals(0.1, ValidationUtils.requirePositive(0.1, "testField"));

    assertThrows(ValidationException.class, () -> ValidationUtils.requirePositive(0, "testField"));
    assertThrows(ValidationException.class, () -> ValidationUtils.requirePositive(-5, "testField"));
  }

  @Test
  void shouldRequireValidEmail() {
    assertEquals("test@example.com", ValidationUtils.requireValidEmail("test@example.com"));
    assertEquals("test@example.com", ValidationUtils.requireValidEmail("  Test@Example.COM  "));

    assertThrows(
        ValidationException.class, () -> ValidationUtils.requireValidEmail("invalid-email"));
    assertThrows(ValidationException.class, () -> ValidationUtils.requireValidEmail(""));
  }

  @Test
  void shouldRequireValidPassword() {
    String validPassword = "SecureP@ss1";
    assertEquals(validPassword, ValidationUtils.requireValidPassword(validPassword));

    assertThrows(ValidationException.class, () -> ValidationUtils.requireValidPassword("weak"));
    assertThrows(ValidationException.class, () -> ValidationUtils.requireValidPassword(""));
  }
}
