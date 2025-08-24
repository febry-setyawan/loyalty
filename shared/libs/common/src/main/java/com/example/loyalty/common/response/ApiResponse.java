package com.example.loyalty.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API response wrapper for all loyalty system endpoints Ensures consistent response format
 * across all services
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private boolean success;
  private T data;
  private String message;
  private ErrorDetails error;
  private LocalDateTime timestamp;

  private ApiResponse(boolean success, T data, String message, ErrorDetails error) {
    this.success = success;
    this.data = data;
    this.message = message;
    this.error = error;
    this.timestamp = LocalDateTime.now();
  }

  /** Create successful response with data */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, data, null, null);
  }

  /** Create successful response with data and message */
  public static <T> ApiResponse<T> success(T data, String message) {
    return new ApiResponse<>(true, data, message, null);
  }

  /** Create error response */
  public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(false, null, null, new ErrorDetails(code, message, null, null));
  }

  /** Create error response with correlation ID */
  public static <T> ApiResponse<T> error(String code, String message, String correlationId) {
    return new ApiResponse<>(
        false, null, null, new ErrorDetails(code, message, correlationId, null));
  }

  /** Create validation error response */
  public static <T> ApiResponse<T> validationError(String message, List<ValidationError> details) {
    return new ApiResponse<>(
        false, null, null, new ErrorDetails("VALIDATION_ERROR", message, null, details));
  }

  /** Create validation error response with correlation ID */
  public static <T> ApiResponse<T> validationError(
      String message, List<ValidationError> details, String correlationId) {
    return new ApiResponse<>(
        false, null, null, new ErrorDetails("VALIDATION_ERROR", message, correlationId, details));
  }

  // Getters
  public boolean isSuccess() {
    return success;
  }

  public T getData() {
    return data;
  }

  public String getMessage() {
    return message;
  }

  public ErrorDetails getError() {
    return error;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /** Error details structure for API responses */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ErrorDetails {
    private String code;
    private String message;
    private String correlationId;
    private List<ValidationError> details;

    public ErrorDetails(
        String code, String message, String correlationId, List<ValidationError> details) {
      this.code = code;
      this.message = message;
      this.correlationId = correlationId;
      this.details = details;
    }

    // Getters
    public String getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

    public String getCorrelationId() {
      return correlationId;
    }

    public List<ValidationError> getDetails() {
      return details;
    }
  }

  /** Validation error details */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ValidationError {
    private String field;
    private String message;

    public ValidationError(String field, String message) {
      this.field = field;
      this.message = message;
    }

    public String getField() {
      return field;
    }

    public String getMessage() {
      return message;
    }
  }
}
