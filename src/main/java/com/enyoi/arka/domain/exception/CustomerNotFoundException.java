package com.enyoi.arka.domain.exception;

public class CustomerNotFoundException extends ArkaDomainException {
    public CustomerNotFoundException(String customerId) {
        super("Customer not found: " + customerId);
    }
}