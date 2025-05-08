package com.belman.business.richbe.exceptions;

/**
 * Exception thrown when a value is invalid according to domain rules.
 * This is a domain exception since validation is part of the domain logic.
 */
public class InvalidValueException extends DomainException {

    /**
     * Creates a new InvalidValueException with the specified message.
     *
     * @param message the detail message
     */
    public InvalidValueException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidValueException with the specified field and reason.
     *
     * @param fieldName the name of the field that has an invalid value
     * @param reason    the reason why the value is invalid
     */
    public InvalidValueException(String fieldName, String reason) {
        super("Invalid value for " + fieldName + ": " + reason);
    }

    /**
     * Creates a new InvalidValueException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
}