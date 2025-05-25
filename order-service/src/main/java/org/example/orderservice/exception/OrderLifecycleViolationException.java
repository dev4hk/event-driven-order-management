package org.example.orderservice.exception;

public class OrderLifecycleViolationException extends RuntimeException {
    public OrderLifecycleViolationException(String message) {
        super(message);
    }
}
