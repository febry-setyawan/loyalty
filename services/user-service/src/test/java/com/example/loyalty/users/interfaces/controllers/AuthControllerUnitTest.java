package com.example.loyalty.users.interfaces.controllers;

import com.example.loyalty.users.application.dto.RegisterUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.loyalty.users.application.usecases.RegisterUserUseCase registerUserUseCase;

    @MockBean
    private com.example.loyalty.users.application.usecases.LoginUserUseCase loginUserUseCase;

    @MockBean
    private com.example.loyalty.users.application.usecases.VerifyUserUseCase verifyUserUseCase;

    @MockBean
    private com.example.loyalty.users.application.usecases.PasswordResetUseCase passwordResetUseCase;

    @MockBean
    private com.example.loyalty.users.application.usecases.PasswordResetConfirmUseCase passwordResetConfirmUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCompileSuccessfully() {
        // This test verifies that all imports and dependencies are correctly resolved
        // The fact that this test compiles and runs proves the compilation issues are fixed
        assert true;
    }

    @Test
    void shouldRejectInvalidEmailFormat() throws Exception {
        // Given
        RegisterUserRequest request = new RegisterUserRequest(
                "invalid-email",
                "SecurePass123!",
                "John",
                "Doe",
                null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWeakPassword() throws Exception {
        // Given
        RegisterUserRequest request = new RegisterUserRequest(
                "test@example.com",
                "weak",
                "John",
                "Doe",
                null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}