package com.enyoi.arka.domain.exception;

public class ArkaDomainException extends RuntimeException {
    public ArkaDomainException(String message) {
        super(message);
    }

    public ArkaDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}