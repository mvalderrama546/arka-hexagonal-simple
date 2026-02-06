package com.enyoi.arka.domain.valueobjects;

import java.util.Objects;

public class ProductId {
    private final String value;

    private ProductId(String value) {
        this.value = value;
    }

    public static ProductId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID must not be empty");
        }
        return new ProductId(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ProductId{" + "value='" + value + '\'' + '}';
    }
}
