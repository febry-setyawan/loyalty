package com.example.loyalty.common.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * Common metrics collection for loyalty system Provides standardized metrics across all services
 */
@Component
public class LoyaltyMetrics {

  private final MeterRegistry meterRegistry;

  public LoyaltyMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  /** Increment request counter with labels */
  public void incrementRequests(String method, String endpoint, String statusCode) {
    Counter.builder("loyalty_requests_total")
        .description("Total number of requests")
        .tag("method", method)
        .tag("endpoint", endpoint)
        .tag("status", statusCode)
        .register(meterRegistry)
        .increment();
  }

  /** Increment error counter with labels */
  public void incrementErrors(String errorType, String service, String operation) {
    Counter.builder("loyalty_errors_total")
        .description("Total number of errors")
        .tag("type", errorType)
        .tag("service", service)
        .tag("operation", operation)
        .register(meterRegistry)
        .increment();
  }

  /** Increment business event counter */
  public void incrementBusinessEvent(String eventType, String userTier) {
    Counter.builder("loyalty_business_events_total")
        .description("Total business events processed")
        .tag("event_type", eventType)
        .tag("user_tier", userTier)
        .register(meterRegistry)
        .increment();
  }

  /** Record request timing */
  public Timer.Sample startRequestTimer() {
    return Timer.start(meterRegistry);
  }

  /** Stop timer and record duration */
  public void stopTimer(Timer.Sample sample, String operation, String result) {
    sample.stop(
        Timer.builder("loyalty_operation_duration")
            .description("Operation processing time")
            .tag("operation", operation)
            .tag("result", result)
            .register(meterRegistry));
  }

  /** Create custom counter */
  public Counter createCounter(String name, String description, String... tags) {
    return Counter.builder(name).description(description).tags(tags).register(meterRegistry);
  }

  /** Create custom timer */
  public Timer createTimer(String name, String description, String... tags) {
    return Timer.builder(name).description(description).tags(tags).register(meterRegistry);
  }
}
