package com.belman.business.richbe.events;

/**
 * Business service interface for publishing audit events.
 * <p>
 * This interface defines the contract for publishing audit events to interested subscribers.
 * The implementation details (such as using a message broker, event bus, or in-memory
 * mechanism) are left to the infrastructure layer.
 * <p>
 * The business layer depends on this interface, but the implementation is provided
 * by the infrastructure layer, following the Dependency Inversion Principle.
 */
public interface IAuditPublisher {

    /**
     * Publishes an audit event to all interested subscribers.
     * <p>
     * This method should be non-blocking from the perspective of the publisher.
     * Implementations should handle event delivery asynchronously if necessary.
     *
     * @param event the audit event to publish
     * @throws IllegalArgumentException if the event is not a valid audit event
     */
    void publish(AuditEvent event);

    /**
     * Publishes multiple audit events in a single transaction if supported
     * by the underlying implementation.
     * <p>
     * This method should be non-blocking from the perspective of the publisher.
     * Implementations should handle event delivery asynchronously if necessary.
     *
     * @param events the audit events to publish
     * @throws IllegalArgumentException if any of the events is not a valid audit event
     */
    void publishAll(Iterable<AuditEvent> events);

    /**
     * Publishes an event asynchronously to all registered handlers.
     *
     * @param <T>   the type of event
     * @param event the event to publish
     */
    <T extends AuditEvent> void publishAsync(T event);
}