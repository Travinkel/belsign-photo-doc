package com.belman.domain.shared;

/**
 * Interface for domain event handlers.
 * Implementations of this interface can handle events of a specific type.
 * 
 * @param <T> the type of event this handler can handle
 * @deprecated This interface is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.DomainEventHandler} instead.
 */
@Deprecated
@FunctionalInterface
public interface DomainEventHandler<T extends DomainEvent> extends com.belman.domain.events.DomainEventHandler<T> {
}
