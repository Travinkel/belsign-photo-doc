package com.belman.business.domain.core;

import com.belman.business.domain.events.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 * Base class for all aggregate roots in the domain model.
 * <p>
 * An aggregate root is an entity that acts as the entry point to an aggregate
 * (a cluster of domain objects that are treated as a single unit for data changes).
 * Aggregate roots are responsible for enforcing invariants and consistency rules
 * within the aggregate and exposing domain events that represent significant changes to
 * the aggregate's state.
 *
 * @param <ID> the type of identifier used by this aggregate
 */
public abstract class Aggregate<ID> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private Instant lastModifiedAt;

    /**
     * Returns the identifier of this aggregate root.
     * The identifier must be unique within the aggregate's type.
     *
     * @return the aggregate's identifier
     */
    public abstract ID getId();

    /**
     * Registers a domain event to be dispatched when the aggregate is persisted.
     *
     * @param event the domain event to register
     * @throws NullPointerException if the event is null
     */
    protected void registerDomainEvent(DomainEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        this.domainEvents.add(event);
        updateLastModifiedAt();
    }

    /**
     * Registers multiple domain events to be dispatched when the aggregate is persisted.
     *
     * @param events the collection of domain events to register
     * @throws NullPointerException if the events collection is null or contains null elements
     */
    protected void registerDomainEvents(Collection<DomainEvent> events) {
        Objects.requireNonNull(events, "events collection must not be null");
        events.forEach(this::registerDomainEvent);
    }

    /**
     * Returns and clears all domain events that were registered with this aggregate.
     * This method is typically called by a repository when saving the aggregate.
     *
     * @return an unmodifiable list of domain events
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    /**
     * Returns all registered domain events without clearing the list.
     * This method is useful for testing or inspection purposes.
     *
     * @return an unmodifiable list of domain events
     */
    public List<DomainEvent> getRegisteredDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    /**
     * Checks if there are any registered domain events.
     *
     * @return true if there are registered domain events, false otherwise
     */
    public boolean hasDomainEvents() {
        return !this.domainEvents.isEmpty();
    }



    /**
     * Clears the list of domain events without dispatching them.
     * This is useful in situations where you want to discard events, such as when rolling back a transaction.
     */
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    /**
     * Updates the last modified timestamp of the aggregate.
     */
    protected void updateLastModifiedAt() {
        this.lastModifiedAt = Instant.now();
    }

    /**
     * Gets the last modified timestamp of the aggregate.
     *
     * @return the last modified timestamp
     */
    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    /**
     * Checks equality based on the aggregate's identifier.
     *
     * @param obj the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aggregate<?> that = (Aggregate<?>) obj;
        return Objects.equals(getId(), that.getId());
    }

    /**
     * Computes the hash code based on the aggregate's identifier.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}