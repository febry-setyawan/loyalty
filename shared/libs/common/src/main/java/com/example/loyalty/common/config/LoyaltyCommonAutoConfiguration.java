package com.example.loyalty.common.config;

import com.example.loyalty.common.logging.CorrelationIdFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Auto-configuration for loyalty common components
 * Enables all shared utilities when included in a service
 */
@Configuration
@ComponentScan(basePackages = "com.example.loyalty.common")
@EnableJpaAuditing
public class LoyaltyCommonAutoConfiguration {

    /**
     * Bean validator for input validation
     */
    @Bean
    @ConditionalOnMissingBean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    /**
     * Correlation ID filter for request tracing
     */
    @Bean
    @ConditionalOnMissingBean
    public CorrelationIdFilter correlationIdFilter() {
        return new CorrelationIdFilter();
    }
}