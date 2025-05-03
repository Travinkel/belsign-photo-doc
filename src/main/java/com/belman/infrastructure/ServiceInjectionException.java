package com.belman.infrastructure;

public class ServiceInjectionException extends RuntimeException {
    public ServiceInjectionException(String message) {
        super(message);
    }

    public ServiceInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
