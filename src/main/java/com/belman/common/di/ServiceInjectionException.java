package com.belman.common.di;

public class ServiceInjectionException extends RuntimeException {
    public ServiceInjectionException(String message) {
        super(message);
    }

    public ServiceInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
