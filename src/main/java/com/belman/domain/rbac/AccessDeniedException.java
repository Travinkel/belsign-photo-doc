package com.belman.domain.rbac;

/**
 * Exception thrown when a user doesn't have the required role to access a resource.
 */
public class AccessDeniedException extends RuntimeException {
    
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
     * @param cause the cause
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}