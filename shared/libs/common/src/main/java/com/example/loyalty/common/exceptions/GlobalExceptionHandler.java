package com.example.loyalty.common.exceptions;

import com.example.loyalty.common.response.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for consistent error responses across all services Can be extended or
 * customized by individual services as needed
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(LoyaltyException.class)
  public ResponseEntity<ApiResponse<Object>> handleLoyaltyException(
      LoyaltyException ex, WebRequest request) {

    String correlationId = request.getHeader("X-Correlation-ID");

    logger.error(
        "Loyalty exception occurred",
        "code",
        ex.getCode(),
        "message",
        ex.getMessage(),
        "correlationId",
        correlationId,
        "exception",
        ex);

    ApiResponse<Object> response = ApiResponse.error(ex.getCode(), ex.getMessage(), correlationId);
    return ResponseEntity.status(ex.getStatusCode()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {

    String correlationId = request.getHeader("X-Correlation-ID");

    List<ApiResponse.ValidationError> validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                error ->
                    new ApiResponse.ValidationError(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());

    logger.warn(
        "Validation error occurred", "correlationId", correlationId, "errors", validationErrors);

    ApiResponse<Object> response =
        ApiResponse.validationError("Invalid input data", validationErrors, correlationId);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(
      Exception ex, WebRequest request) {

    String correlationId = request.getHeader("X-Correlation-ID");

    logger.error("Unexpected error occurred", "correlationId", correlationId, "exception", ex);

    ApiResponse<Object> response =
        ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred", correlationId);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
