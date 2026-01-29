package com.enyoi.arka.domain.exception;

public class InsufficientStockException extends ArkaDomainException {
    public InsufficientStockException(String productId, int requested, int available) {
        super("Insufficient stock for product " + productId + ": requested " + requested + ", available " + available);
    }
}