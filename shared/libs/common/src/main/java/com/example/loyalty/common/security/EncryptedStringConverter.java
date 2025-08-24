package com.example.loyalty.common.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA attribute converter for automatic encryption/decryption of sensitive string fields
 * Automatically encrypts data before storing and decrypts when retrieving
 */
@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final EncryptionService encryptionService;

    @Autowired
    public EncryptedStringConverter(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (encryptionService == null || attribute == null) {
            return attribute;
        }
        return encryptionService.encryptSensitiveData(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (encryptionService == null || dbData == null) {
            return dbData;
        }
        return encryptionService.decryptSensitiveData(dbData);
    }
}