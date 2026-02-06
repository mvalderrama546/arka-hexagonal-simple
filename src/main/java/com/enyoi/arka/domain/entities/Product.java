package com.enyoi.arka.domain.entities;

import com.enyoi.arka.domain.valueobjects.Money;
import com.enyoi.arka.domain.valueobjects.ProductId;

import java.util.Objects;

public class Product {
    private final ProductId id;
    private final String name;
    private final String description;
    private final Money price;
    private int stock;
    private final ProductCategory category;

    private Product(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id is required");
        this.name = Objects.requireNonNull(builder.name, "name is required");
        this.description = builder.description;
        this.price = Objects.requireNonNull(builder.price, "price is required");
        this.stock = builder.stock;
        this.category = Objects.requireNonNull(builder.category, "category is required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProductId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void reduceStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalArgumentException("stock exceeded: cannot reduce " + quantity + " from " + stock);
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stock += quantity;
    }

    public boolean isLowStock(int threshold) {
        return stock < threshold;
    }

    public static class Builder {
        private ProductId id;
        private String name;
        private String description;
        private Money price;
        private int stock;
        private ProductCategory category;

        public Builder id(ProductId id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }

        public Builder category(ProductCategory category) {
            this.category = category;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category=" + category +
                '}';
    }
}
