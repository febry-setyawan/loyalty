package com.example.loyalty.users.config;

import com.example.loyalty.common.logging.StructuredLogger;
import com.example.loyalty.users.application.services.EmailService;
import com.example.loyalty.users.application.services.SmsService;
import com.example.loyalty.users.application.services.impl.LoggingEmailService;
import com.example.loyalty.users.application.services.impl.LoggingSmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for tests - disables security and provides test beans
 */
@Configuration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StructuredLogger structuredLogger() {
        return new StructuredLogger(TestSecurityConfig.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // This will register JSR310 module for LocalDateTime
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailService emailService() {
        return new LoggingEmailService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SmsService smsService() {
        return new LoggingSmsService();
    }
}