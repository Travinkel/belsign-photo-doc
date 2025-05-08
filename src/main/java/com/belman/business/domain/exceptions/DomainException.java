package com.belman.business.domain.exceptions;

/**
 * Base exception for all domain-specific exceptions.
 * This class represents errors that occur in the domain layer
 * and are part of the domain logic.
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Constructs a new domain exception with the specified detail message.
     *
     * @param message the detail message
     */
    protected DomainException(String message) {
        super(message);
    }

    /**
     * Constructs a new domain exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}