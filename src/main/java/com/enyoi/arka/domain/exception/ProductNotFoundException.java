package com.enyoi.arka.domain.exception;

public class ProductNotFoundException extends ArkaDomainException {
    public ProductNotFoundException(String productId) {
        super("Product not found: " + productId);
    }
}