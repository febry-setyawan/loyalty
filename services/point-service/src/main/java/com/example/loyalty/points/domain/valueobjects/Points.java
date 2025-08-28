package com.example.loyalty.points.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing points in the loyalty system
 */
public class Points {
    private final BigDecimal value;

    public Points(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Points value cannot be null or negative");
        }
        this.value = value;
    }

    public Points(long value) {
        this(BigDecimal.valueOf(value));
    }

    public BigDecimal getValue() {
        return value;
    }

    public Points add(Points other) {
        return new Points(this.value.add(other.value));
    }

    public Points subtract(Points other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        return new Points(result);
    }

    public Points multiply(BigDecimal multiplier) {
        if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        return new Points(this.value.multiply(multiplier));
    }

    public boolean isGreaterThan(Points other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isGreaterThanOrEqual(Points other) {
        return this.value.compareTo(other.value) >= 0;
    }

    public long longValue() {
        return value.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Points points = (Points) o;
        return Objects.equals(value, points.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Points{" + "value=" + value + '}';
    }

    public static Points zero() {
        return new Points(BigDecimal.ZERO);
    }
}