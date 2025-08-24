package com.example.loyalty.common.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncryptionServiceTest {

  private EncryptionService encryptionService;
  private String testEncryptionKey;

  @BeforeEach
  void setUp() {
    // Generate a test key
    testEncryptionKey = EncryptionService.generateEncryptionKey();
    encryptionService = new EncryptionService(testEncryptionKey);
  }

  @Test
  void shouldGenerateValidEncryptionKey() {
    String key = EncryptionService.generateEncryptionKey();

    assertNotNull(key);

    // Should be base64 encoded
    byte[] decoded = Base64.getDecoder().decode(key);
    assertEquals(32, decoded.length); // 256 bits = 32 bytes
  }

  @Test
  void shouldEncryptAndDecryptData() {
    String originalData = "sensitive information";

    String encrypted = encryptionService.encryptSensitiveData(originalData);
    String decrypted = encryptionService.decryptSensitiveData(encrypted);

    assertNotNull(encrypted);
    assertNotEquals(originalData, encrypted);
    assertEquals(originalData, decrypted);
  }

  @Test
  void shouldReturnNullForNullInput() {
    assertNull(encryptionService.encryptSensitiveData(null));
    assertNull(encryptionService.decryptSensitiveData(null));
  }

  @Test
  void shouldReturnEmptyForEmptyInput() {
    assertEquals("", encryptionService.encryptSensitiveData(""));
    assertEquals("", encryptionService.decryptSensitiveData(""));
  }

  @Test
  void shouldProduceDifferentEncryptionForSameData() {
    String data = "test data";

    String encrypted1 = encryptionService.encryptSensitiveData(data);
    String encrypted2 = encryptionService.encryptSensitiveData(data);

    // Should be different due to random IV
    assertNotEquals(encrypted1, encrypted2);

    // But both should decrypt to original
    assertEquals(data, encryptionService.decryptSensitiveData(encrypted1));
    assertEquals(data, encryptionService.decryptSensitiveData(encrypted2));
  }

  @Test
  void shouldThrowExceptionForInvalidKey() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new EncryptionService(""); // Empty key
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new EncryptionService("invalid-base64-key!@#");
        });

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new EncryptionService(
              Base64.getEncoder()
                  .encodeToString(new byte[16])); // Wrong size (128 bit instead of 256)
        });
  }

  @Test
  void shouldThrowExceptionForInvalidEncryptedData() {
    assertThrows(
        SecurityException.class,
        () -> {
          encryptionService.decryptSensitiveData("invalid-encrypted-data");
        });

    assertThrows(
        SecurityException.class,
        () -> {
          encryptionService.decryptSensitiveData(
              "dGVzdA=="); // Valid base64 but invalid encryption format
        });
  }

  @Test
  void shouldHandleLongData() {
    // Test with long string
    String longData = "a".repeat(10000);

    String encrypted = encryptionService.encryptSensitiveData(longData);
    String decrypted = encryptionService.decryptSensitiveData(encrypted);

    assertEquals(longData, decrypted);
  }

  @Test
  void shouldHandleSpecialCharacters() {
    String specialData = "éñül@#$%^&*(){}[]|\\:;\"'<>,.?/~`!+=\n\t\r";

    String encrypted = encryptionService.encryptSensitiveData(specialData);
    String decrypted = encryptionService.decryptSensitiveData(encrypted);

    assertEquals(specialData, decrypted);
  }
}
