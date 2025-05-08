package com.belman.business.domain.events;

/**
 * Domain service interface for publishing domain events.
 * <p>
 * This interface defines the contract for publishing domain events to interested subscribers.
 * The implementation details (such as using a message broker, event bus, or in-memory
 * mechanism) are left to the infrastructure layer.
 * <p>
 * The domain layer depends on this interface, but the implementation is provided
 * by the infrastructure layer, following the Dependency Inversion Principle.
 */
public interface IDomainEventPublisher {

    /**
     * Publishes a domain event to all interested subscribers.
     * <p>
     * This method should be non-blocking from the perspective of the publisher.
     * Implementations should handle event delivery asynchronously if necessary.
     *
     * @param event the domain event to publish
     * @throws IllegalArgumentException if the event is not a valid domain event
     */
    void publish(DomainEvent event);

    /**
     * Publishes multiple domain events in a single transaction if supported
     * by the underlying implementation.
     * <p>
     * This method should be non-blocking from the perspective of the publisher.
     * Implementations should handle event delivery asynchronously if necessary.
     *
     * @param events the domain events to publish
     * @throws IllegalArgumentException if any of the events is not a valid domain event
     */
    void publishAll(Iterable<DomainEvent> events);

    /**
     * Publishes an event asynchronously to all registered handlers.
     *
     * @param <T>   the type of event
     * @param event the event to publish
     */
    <T extends DomainEvent> void publishAsync(T event);
}