package com.example.loyalty.common.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * AES-256-GCM encryption service for sensitive data protection Provides encryption/decryption
 * utilities for PII and sensitive fields
 */
@Service
public class EncryptionService {

  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 16;

  private final SecretKey encryptionKey;
  private final SecureRandom secureRandom;

  public EncryptionService(@Value("${app.encryption.key:}") String encryptionKeyBase64) {
    this.secureRandom = new SecureRandom();

    if (!StringUtils.hasText(encryptionKeyBase64)) {
      throw new IllegalArgumentException(
          "Encryption key must be provided via app.encryption.key property");
    }

    try {
      byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
      if (keyBytes.length != 32) { // 256 bits
        throw new IllegalArgumentException(
            "Encryption key must be 256 bits (32 bytes) when base64 decoded");
      }
      this.encryptionKey = new SecretKeySpec(keyBytes, ALGORITHM);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Invalid encryption key format. Must be base64 encoded 256-bit key", e);
    }
  }

  /** Encrypt sensitive data using AES-256-GCM */
  public String encryptSensitiveData(String data) {
    if (!StringUtils.hasText(data)) {
      return data;
    }

    try {
      // Generate random IV
      byte[] iv = new byte[GCM_IV_LENGTH];
      secureRandom.nextBytes(iv);

      // Initialize cipher
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
      cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, gcmParameterSpec);

      // Encrypt data
      byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

      // Combine IV + encrypted data
      byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
      System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
      System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);

      // Return base64 encoded result
      return Base64.getEncoder().encodeToString(encryptedWithIv);

    } catch (Exception e) {
      throw new SecurityException("Data encryption failed", e);
    }
  }

  /** Decrypt sensitive data using AES-256-GCM */
  public String decryptSensitiveData(String encryptedData) {
    if (!StringUtils.hasText(encryptedData)) {
      return encryptedData;
    }

    try {
      // Decode from base64
      byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedData);

      if (encryptedWithIv.length < GCM_IV_LENGTH) {
        throw new IllegalArgumentException("Invalid encrypted data format");
      }

      // Extract IV and encrypted data
      byte[] iv = new byte[GCM_IV_LENGTH];
      byte[] encrypted = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
      System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
      System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

      // Initialize cipher for decryption
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
      cipher.init(Cipher.DECRYPT_MODE, encryptionKey, gcmParameterSpec);

      // Decrypt data
      byte[] decryptedData = cipher.doFinal(encrypted);

      return new String(decryptedData, StandardCharsets.UTF_8);

    } catch (Exception e) {
      throw new SecurityException("Data decryption failed", e);
    }
  }

  /** Generate a new 256-bit encryption key (for setup/rotation) */
  public static String generateEncryptionKey() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
      keyGenerator.init(256);
      SecretKey key = keyGenerator.generateKey();
      return Base64.getEncoder().encodeToString(key.getEncoded());
    } catch (Exception e) {
      throw new SecurityException("Failed to generate encryption key", e);
    }
  }
}
