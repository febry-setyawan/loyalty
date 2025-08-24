package com.example.loyalty.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataMaskingServiceTest {

    private DataMaskingService dataMaskingService;

    @BeforeEach
    void setUp() {
        dataMaskingService = new DataMaskingService();
    }

    @Test
    void shouldMaskCreditCard() {
        assertEquals("**** **** **** 3456", dataMaskingService.maskCreditCard("1234567890123456"));
        assertEquals("**** **** **** 9012", dataMaskingService.maskCreditCard("4532-1234-5678-9012"));
    }

    @Test
    void shouldHandleInvalidCreditCard() {
        assertEquals("123", dataMaskingService.maskCreditCard("123")); // Too short
        assertEquals("", dataMaskingService.maskCreditCard(""));
        assertEquals(null, dataMaskingService.maskCreditCard(null));
    }

    @Test
    void shouldMaskEmail() {
        assertEquals("t**t@example.com", dataMaskingService.maskEmail("test@example.com"));
        assertEquals("j**e@company.org", dataMaskingService.maskEmail("jane@company.org"));
        assertEquals("a@test.com", dataMaskingService.maskEmail("a@test.com")); // Single char username
    }

    @Test
    void shouldHandleInvalidEmail() {
        assertEquals("invalid-email", dataMaskingService.maskEmail("invalid-email"));
        assertEquals("", dataMaskingService.maskEmail(""));
        assertEquals(null, dataMaskingService.maskEmail(null));
    }

    @Test
    void shouldMaskPhoneNumber() {
        assertEquals("******7890", dataMaskingService.maskPhoneNumber("1234567890"));
        assertEquals("**********5678", dataMaskingService.maskPhoneNumber("+1234567895678"));
    }

    @Test
    void shouldHandleInvalidPhoneNumber() {
        assertEquals("123", dataMaskingService.maskPhoneNumber("123")); // Too short
        assertEquals("", dataMaskingService.maskPhoneNumber(""));
        assertEquals(null, dataMaskingService.maskPhoneNumber(null));
    }

    @Test
    void shouldMaskName() {
        assertEquals("J*** S.", dataMaskingService.maskName("John Smith"));
        assertEquals("J*** M. D.", dataMaskingService.maskName("John Michael Doe"));
        assertEquals("A****", dataMaskingService.maskName("Alice"));
        assertEquals("J", dataMaskingService.maskName("J"));
    }

    @Test
    void shouldHandleInvalidName() {
        assertEquals("", dataMaskingService.maskName(""));
        assertEquals("   ", dataMaskingService.maskName("   ")); // Whitespace only - this doesn't get trimmed by maskName
        assertEquals(null, dataMaskingService.maskName(null));
    }

    @Test
    void shouldMaskSSN() {
        assertEquals("***-**-6789", dataMaskingService.maskSSN("123-45-6789"));
        assertEquals("***-**-4321", dataMaskingService.maskSSN("987654321")); // No dashes
    }

    @Test
    void shouldHandleInvalidSSN() {
        assertEquals("123", dataMaskingService.maskSSN("123")); // Too short
        assertEquals("", dataMaskingService.maskSSN(""));
        assertEquals(null, dataMaskingService.maskSSN(null));
    }

    @Test
    void shouldMaskAddress() {
        assertEquals("123 *** ****, USA", dataMaskingService.maskAddress("123 Main Street, Anytown, USA"));
        assertEquals("456 *** ****, Canada", dataMaskingService.maskAddress("456 Oak Ave, Toronto, Canada"));
    }

    @Test
    void shouldHandleInvalidAddress() {
        assertEquals("*** *** ***", dataMaskingService.maskAddress("No commas here"));
        assertEquals("", dataMaskingService.maskAddress(""));
        assertEquals(null, dataMaskingService.maskAddress(null));
    }

    @Test
    void shouldMaskGeneric() {
        assertEquals("te***ng", dataMaskingService.maskGeneric("testing", 2, 2));
        assertEquals("a***e", dataMaskingService.maskGeneric("apple", 1, 1));
        assertEquals("1234****7890", dataMaskingService.maskGeneric("123456787890", 4, 4));
    }

    @Test
    void shouldHandleGenericMaskingEdgeCases() {
        // Data too short for requested masking
        assertEquals("ab", dataMaskingService.maskGeneric("ab", 1, 1));
        assertEquals("abc", dataMaskingService.maskGeneric("abc", 2, 2));
        
        assertEquals("", dataMaskingService.maskGeneric("", 1, 1));
        assertEquals(null, dataMaskingService.maskGeneric(null, 1, 1));
    }

    @Test
    void shouldPreserveSensitiveDataStructure() {
        // Test that masking preserves recognizable structure
        String email = "user@domain.com";
        String maskedEmail = dataMaskingService.maskEmail(email);
        assertTrue(maskedEmail.contains("@"));
        assertTrue(maskedEmail.endsWith("domain.com"));
        
        String phone = "123-456-7890";
        String maskedPhone = dataMaskingService.maskPhoneNumber(phone);
        assertTrue(maskedPhone.endsWith("7890"));
        
        String ssn = "123-45-6789";
        String maskedSSN = dataMaskingService.maskSSN(ssn);
        assertTrue(maskedSSN.endsWith("6789"));
        assertTrue(maskedSSN.startsWith("***-**-"));
    }
}