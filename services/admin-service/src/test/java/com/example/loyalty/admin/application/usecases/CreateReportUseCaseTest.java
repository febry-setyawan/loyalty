package com.example.loyalty.admin.application.usecases;

import com.example.loyalty.admin.domain.entities.Report;
import com.example.loyalty.admin.domain.entities.ReportType;
import com.example.loyalty.admin.domain.entities.ReportStatus;
import com.example.loyalty.admin.domain.repositories.ReportRepository;
import com.example.loyalty.admin.application.dto.CreateReportRequest;
import com.example.loyalty.admin.application.dto.ReportDTO;
import com.example.loyalty.common.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateReportUseCase
 * Tests report creation business logic and validation
 */
@ExtendWith(MockitoExtension.class)
class CreateReportUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private CreateReportUseCase createReportUseCase;

    private UUID testCreatorId;
    private CreateReportRequest validRequest;

    @BeforeEach
    void setUp() {
        testCreatorId = UUID.randomUUID();
        
        validRequest = CreateReportRequest.builder()
                .name("Test Correlation Report")
                .description("Test description")
                .type(ReportType.CORRELATION_ANALYSIS)
                .createdBy(testCreatorId)
                .build();
    }

    @Test
    @DisplayName("Should create report successfully when valid data provided")
    void should_create_report_successfully_when_valid_data_provided() {
        // Given
        Report savedReport = new Report(
                validRequest.getName(),
                validRequest.getType(),
                validRequest.getCreatedBy()
        );
        savedReport.setDescription(validRequest.getDescription());
        
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        // When
        ReportDTO result = createReportUseCase.execute(validRequest);

        // Then
        assertNotNull(result);
        assertEquals(validRequest.getName(), result.getName());
        assertEquals(validRequest.getDescription(), result.getDescription());
        assertEquals(validRequest.getType(), result.getType());
        assertEquals(validRequest.getCreatedBy(), result.getCreatedBy());
        assertEquals(ReportStatus.DRAFT, result.getStatus());

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Should create report with configuration when provided")
    void should_create_report_with_configuration_when_provided() {
        // Given
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("xVariable", "points_earned");
        configuration.put("yVariable", "customer_satisfaction");
        
        validRequest.setConfiguration(configuration);
        
        Report savedReport = new Report(
                validRequest.getName(),
                validRequest.getType(),
                validRequest.getCreatedBy()
        );
        
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        // When
        ReportDTO result = createReportUseCase.execute(validRequest);

        // Then
        assertNotNull(result);
        verify(reportRepository).save(argThat(report -> 
                report.getConfiguration() != null &&
                report.getConfiguration().get("xVariable").equals("points_earned")
        ));
    }

    @Test
    @DisplayName("Should throw ValidationException when request is null")
    void should_throw_validation_exception_when_request_is_null() {
        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when name is null")
    void should_throw_validation_exception_when_name_is_null() {
        // Given
        validRequest.setName(null);

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Report name is required", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when name is empty")
    void should_throw_validation_exception_when_name_is_empty() {
        // Given
        validRequest.setName("   ");

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Report name is required", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when name is too long")
    void should_throw_validation_exception_when_name_is_too_long() {
        // Given
        validRequest.setName("A".repeat(256)); // 256 characters

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Report name is too long", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when type is null")
    void should_throw_validation_exception_when_type_is_null() {
        // Given
        validRequest.setType(null);

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Report type is required", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when creator ID is null")
    void should_throw_validation_exception_when_creator_id_is_null() {
        // Given
        validRequest.setCreatedBy(null);

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Creator ID is required", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when description is too long")
    void should_throw_validation_exception_when_description_is_too_long() {
        // Given
        validRequest.setDescription("A".repeat(1001)); // 1001 characters

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Description is too long", exception.getMessage());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should create report with statistical config when provided")
    void should_create_report_with_statistical_config_when_provided() {
        // Given
        Map<String, Object> statisticalConfig = new HashMap<>();
        statisticalConfig.put("correlationThreshold", 0.5);
        statisticalConfig.put("significanceLevel", 0.05);
        
        validRequest.setStatisticalConfig(statisticalConfig);
        
        Report savedReport = new Report(
                validRequest.getName(),
                validRequest.getType(),
                validRequest.getCreatedBy()
        );
        
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        // When
        ReportDTO result = createReportUseCase.execute(validRequest);

        // Then
        assertNotNull(result);
        verify(reportRepository).save(argThat(report -> 
                report.getStatisticalConfig() != null &&
                report.getStatisticalConfig().get("correlationThreshold").equals(0.5)
        ));
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void should_handle_repository_exception_gracefully() {
        // Given
        when(reportRepository.save(any(Report.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createReportUseCase.execute(validRequest)
        );

        assertEquals("Failed to create report", exception.getMessage());
        assertTrue(exception.getCause().getMessage().contains("Database error"));
    }
}