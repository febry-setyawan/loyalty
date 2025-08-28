package com.example.loyalty.points.infrastructure.config;

import com.example.loyalty.points.domain.repositories.EarningRuleRepository;
import com.example.loyalty.points.domain.services.PointCalculationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain services
 */
@Configuration
public class DomainConfig {

    @Bean
    public PointCalculationService pointCalculationService(EarningRuleRepository earningRuleRepository) {
        return new PointCalculationService(earningRuleRepository);
    }
}