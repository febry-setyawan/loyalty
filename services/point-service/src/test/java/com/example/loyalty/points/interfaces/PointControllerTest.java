package com.example.loyalty.points.interfaces;

import com.example.loyalty.points.application.dto.EarnPointsDTO;
import com.example.loyalty.points.application.usecases.EarnPointsUseCase;
import com.example.loyalty.points.application.usecases.GetBalanceUseCase;
import com.example.loyalty.points.config.TestSecurityConfig;
import com.example.loyalty.points.domain.repositories.EarningRuleRepository;
import com.example.loyalty.common.exceptions.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EarnPointsUseCase earnPointsUseCase;

    @MockBean
    private GetBalanceUseCase getBalanceUseCase;

    @MockBean
    private EarningRuleRepository earningRuleRepository;

    @Test
    void testEarnPoints_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        EarnPointsDTO.Request request = new EarnPointsDTO.Request(
            userId, 
            BigDecimal.valueOf(5000), 
            "PURCHASE"
        );
        
        EarnPointsDTO.Response mockResponse = new EarnPointsDTO.Response(
            UUID.randomUUID().toString(),
            userId,
            5L,  // 5 points earned
            1505L, // new balance
            "Points earned successfully"
        );

        when(earnPointsUseCase.execute(any(EarnPointsDTO.Request.class)))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/points/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pointsEarned").value(5))
                .andExpect(jsonPath("$.data.newBalance").value(1505))
                .andExpect(jsonPath("$.message").value("Points earned successfully"));
    }

    @Test
    void testEarnPoints_InvalidRequest() throws Exception {
        // Given - invalid request (negative amount)
        UUID userId = UUID.randomUUID();
        EarnPointsDTO.Request request = new EarnPointsDTO.Request(
            userId, 
            BigDecimal.valueOf(-1000), 
            "PURCHASE"
        );

        when(earnPointsUseCase.execute(any(EarnPointsDTO.Request.class)))
            .thenThrow(new IllegalArgumentException("Transaction amount must be positive"));

        // When & Then
        mockMvc.perform(post("/api/v1/points/earn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Invalid input data"));
    }

    @Test
    void testGetBalance_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/v1/points/balance/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEarningRules_Success() throws Exception {
        // Given
        when(earningRuleRepository.findActiveRules()).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/api/v1/points/earning-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testAwardReferralPoints_Success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        EarnPointsDTO.Request request = new EarnPointsDTO.Request();
        request.setUserId(userId);
        request.setTransactionAmount(BigDecimal.valueOf(1)); // Dummy amount for referral
        
        EarnPointsDTO.Response mockResponse = new EarnPointsDTO.Response(
            UUID.randomUUID().toString(),
            userId,
            500L,  // 500 referral points
            2000L, // new balance
            "Referral points awarded successfully"
        );

        when(earnPointsUseCase.execute(any(EarnPointsDTO.Request.class)))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/points/referral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pointsEarned").value(500));
    }
}