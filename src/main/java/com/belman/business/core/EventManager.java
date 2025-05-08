package com.belman.business.core;

import com.belman.business.richbe.events.DomainEvent;
import com.belman.business.richbe.events.DomainEventHandler;
import com.belman.business.richbe.events.DomainEventPublisher;

/**
 * Manager for domain events.
 * Provides methods for publishing events and registering event handlers.
 * This class is a wrapper around the DomainEventPublisher class and provides a more convenient API.
 */
public class EventManager {
    private static final EventManager instance = new EventManager();

    private EventManager() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Gets the singleton instance of the EventManager.
     * 
     * @return the EventManager instance
     */
    public static EventManager getInstance() {
        return instance;
    }

    /**
     * Registers a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to register
     * @param <T> the type of event
     */
    public <T extends DomainEvent> void registerEventHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        DomainEventPublisher.getInstance().register(eventType, handler);
    }

    /**
     * Unregisters a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to unregister
     * @param <T> the type of event
     */
    public <T extends DomainEvent> void unregisterEventHandler(Class<T> eventType, DomainEventHandler<T> handler) {
        DomainEventPublisher.getInstance().unregister(eventType, handler);
    }

    /**
     * Publishes an event to all registered handlers.
     * 
     * @param event the event to publish
     * @param <T> the type of event
     */
    public <T extends DomainEvent> void publishEvent(T event) {
        DomainEventPublisher.getInstance().publish(event);
    }

    /**
     * Publishes an event asynchronously to all registered handlers.
     * 
     * @param event the event to publish
     * @param <T> the type of event
     */
    public <T extends DomainEvent> void publishEventAsync(T event) {
        DomainEventPublisher.getInstance().publishAsync(event);
    }
}
