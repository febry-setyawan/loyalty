package com.example.loyalty.common.logging;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Structured logging utility for consistent logging across all services Provides methods for adding
 * structured context to logs
 */
public class StructuredLogger {

  private final Logger logger;

  public StructuredLogger(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  public static StructuredLogger getLogger(Class<?> clazz) {
    return new StructuredLogger(clazz);
  }

  /** Log info with structured context */
  public void info(String message, Object... keyValuePairs) {
    logWithContext(() -> logger.info(message), keyValuePairs);
  }

  /** Log warn with structured context */
  public void warn(String message, Object... keyValuePairs) {
    logWithContext(() -> logger.warn(message), keyValuePairs);
  }

  /** Log error with structured context */
  public void error(String message, Object... keyValuePairs) {
    logWithContext(() -> logger.error(message), keyValuePairs);
  }

  /** Log error with exception and structured context */
  public void error(String message, Throwable throwable, Object... keyValuePairs) {
    logWithContext(() -> logger.error(message, throwable), keyValuePairs);
  }

  /** Log debug with structured context */
  public void debug(String message, Object... keyValuePairs) {
    logWithContext(() -> logger.debug(message), keyValuePairs);
  }

  /** Log an action start with timing context */
  public TimingContext startTiming(String action, Object... keyValuePairs) {
    String timingId = java.util.UUID.randomUUID().toString().substring(0, 8);
    MDC.put("timingId", timingId);

    logWithContext(() -> logger.info("Action started: " + action), keyValuePairs);

    return new TimingContext(action, timingId, System.currentTimeMillis(), this);
  }

  /** Helper method to add context to MDC and execute logging */
  private void logWithContext(Runnable logAction, Object... keyValuePairs) {
    Map<String, String> originalMdc = MDC.getCopyOfContextMap();

    try {
      // Add key-value pairs to MDC
      for (int i = 0; i < keyValuePairs.length; i += 2) {
        if (i + 1 < keyValuePairs.length) {
          String key = String.valueOf(keyValuePairs[i]);
          String value = String.valueOf(keyValuePairs[i + 1]);
          MDC.put(key, value);
        }
      }

      // Execute logging
      logAction.run();

    } finally {
      // Restore original MDC
      MDC.clear();
      if (originalMdc != null) {
        MDC.setContextMap(originalMdc);
      }
    }
  }

  /** Inner class for timing operations */
  public static class TimingContext {
    private final String action;
    private final String timingId;
    private final long startTime;
    private final StructuredLogger logger;

    public TimingContext(String action, String timingId, long startTime, StructuredLogger logger) {
      this.action = action;
      this.timingId = timingId;
      this.startTime = startTime;
      this.logger = logger;
    }

    /** End timing and log duration */
    public void end(Object... keyValuePairs) {
      long duration = System.currentTimeMillis() - startTime;

      Object[] contextWithTiming = new Object[keyValuePairs.length + 4];
      contextWithTiming[0] = "timingId";
      contextWithTiming[1] = timingId;
      contextWithTiming[2] = "duration";
      contextWithTiming[3] = duration + "ms";

      System.arraycopy(keyValuePairs, 0, contextWithTiming, 4, keyValuePairs.length);

      logger.info("Action completed: " + action, contextWithTiming);
    }

    /** End timing with error and log duration */
    public void endWithError(String errorMessage, Object... keyValuePairs) {
      long duration = System.currentTimeMillis() - startTime;

      Object[] contextWithTiming = new Object[keyValuePairs.length + 6];
      contextWithTiming[0] = "timingId";
      contextWithTiming[1] = timingId;
      contextWithTiming[2] = "duration";
      contextWithTiming[3] = duration + "ms";
      contextWithTiming[4] = "status";
      contextWithTiming[5] = "error";

      System.arraycopy(keyValuePairs, 0, contextWithTiming, 6, keyValuePairs.length);

      logger.error("Action failed: " + action + " - " + errorMessage, contextWithTiming);
    }
  }
}
