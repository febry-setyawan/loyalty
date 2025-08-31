package com.example.loyalty.admin.config;

import com.example.loyalty.admin.domain.repositories.ReportRepository;
import com.example.loyalty.admin.infrastructure.persistence.repositories.ReportRepositoryImpl;
import com.example.loyalty.admin.infrastructure.persistence.repositories.JpaReportRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the admin service
 * Sets up repository implementations and other beans
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.loyalty.admin.infrastructure.persistence.repositories")
@EnableTransactionManagement
public class AdminServiceConfiguration {
    
    /**
     * Configure the domain repository implementation
     */
    @Bean
    public ReportRepository reportRepository(JpaReportRepository jpaReportRepository) {
        return new ReportRepositoryImpl(jpaReportRepository);
    }
}