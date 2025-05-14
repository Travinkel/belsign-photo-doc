package com.belman.ui.events;

/**
 * Interface for an event subscription.
 * This is part of the Mediator pattern for component communication.
 */
public interface Subscription {
    /**
     * Unsubscribes from the event.
     */
    void unsubscribe();
}