package com.belman.domain.exceptions;

/**
 * Base exception for all business-specific exceptions.
 * This class represents errors that occur in the business layer
 * and are part of the business logic.
 */
public abstract class BusinessException extends RuntimeException {

    /**
     * Constructs a new business exception with the specified detail message.
     *
     * @param message the detail message
     */
    protected BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new business exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}