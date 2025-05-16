package com.belman.bootstrap.event;

import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.audit.event.AuditHandler;
import com.belman.domain.audit.event.AuditPublisher;

/**
 * Manager for audit events.
 * Provides methods for publishing events and registering event handlers.
 * This class is a wrapper around the AuditPublisher class and provides a more convenient API.
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
     * @param handler   the handler to register
     * @param <T>       the type of event
     */
    public <T extends AuditEvent> void registerEventHandler(Class<T> eventType, AuditHandler<T> handler) {
        AuditPublisher.getInstance().register(eventType, handler);
    }

    /**
     * Unregisters a handler for a specific event type.
     *
     * @param eventType the class of the event type
     * @param handler   the handler to unregister
     * @param <T>       the type of event
     */
    public <T extends AuditEvent> void unregisterEventHandler(Class<T> eventType, AuditHandler<T> handler) {
        AuditPublisher.getInstance().unregister(eventType, handler);
    }

    /**
     * Publishes an event to all registered handlers.
     *
     * @param event the event to publish
     * @param <T>   the type of event
     */
    public <T extends AuditEvent> void publishEvent(T event) {
        AuditPublisher.getInstance().publish(event);
    }

    /**
     * Publishes an event asynchronously to all registered handlers.
     *
     * @param event the event to publish
     * @param <T>   the type of event
     */
    public <T extends AuditEvent> void publishEventAsync(T event) {
        AuditPublisher.getInstance().publishAsync(event);
    }
}
