package com.belman.domain.exceptions;

/**
 * Exception thrown when an entity is not found.
 * This is a domain exception since the existence of an entity
 * is part of the domain logic.
 */
public class EntityNotFoundException extends DomainException {

    /**
     * Creates a new EntityNotFoundException with the specified message.
     *
     * @param entityType the type of entity that was not found
     * @param id         the ID of the entity that was not found
     */
    public EntityNotFoundException(String entityType, String id) {
        super(entityType + " with ID " + id + " not found");
    }

    /**
     * Creates a new EntityNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new EntityNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}