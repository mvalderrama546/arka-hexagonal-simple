package com.enyoi.arka.domain.valueobjects;

import java.util.Objects;

public class OrderId {
    private final String value;

    private OrderId(String value) {
        this.value = value;
    }

    public static OrderId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID must not be empty");
        }
        return new OrderId(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "OrderId{" + "value='" + value + '\'' + '}';
    }
}
