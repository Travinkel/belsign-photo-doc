package com.belman.domain.audit.event;

/**
 * Interface for audit event handlers.
 * Implementations of this interface can handle events of a specific type.
 *
 * @param <T> the type of event this handler can handle
 */
@FunctionalInterface
public interface AuditHandler<T extends AuditEvent> {

    /**
     * Handles the specified event.
     *
     * @param event the event to handle
     */
    void handle(T event);
}