package com.belman.business.domain.exceptions;

/**
 * Exception thrown when a user doesn't have the required role to access a resource.
 * This is a domain exception since access control is part of the domain logic.
 */
public class AccessDeniedException extends DomainException {

    /**
     * Creates a new AccessDeniedException with the specified message.
     *
     * @param message the detail message
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Creates a new AccessDeniedException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}