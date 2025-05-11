package com.belman.domain.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Publisher for business events.
 * <p>
 * This class is responsible for publishing business events to registered handlers.
 * It follows the publisher-subscriber pattern, where handlers subscribe to specific
 * event types and are notified when events of those types are published.
 */
public class BusinessEventPublisher {

    private static final BusinessEventPublisher INSTANCE = new BusinessEventPublisher();
    private final Map<Class<? extends BusinessEvent>, List<BusinessEventHandler<?>>> handlers = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private BusinessEventPublisher() {
    }

    /**
     * Gets the singleton instance of the business event publisher.
     *
     * @return the singleton instance
     */
    public static BusinessEventPublisher getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a handler for a specific type of business event.
     *
     * @param eventType the type of business event to handle
     * @param handler   the handler to register
     * @param <T>       the type of business event
     */
    public <T extends BusinessEvent> void register(Class<T> eventType, BusinessEventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    /**
     * Publishes multiple business events.
     *
     * @param events the business events to publish
     */
    public void publishAll(Iterable<BusinessEvent> events) {
        if (events == null) return;

        for (BusinessEvent event : events) {
            publish(event);
        }
    }

    /**
     * Publishes a business event to all registered handlers for its type.
     *
     * @param event the business event to publish
     */
    public void publish(BusinessEvent event) {
        if (event == null) return;

        List<BusinessEventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (BusinessEventHandler handler : eventHandlers) {
                handler.handle(event);
            }
        }
    }

    /**
     * Publishes a business event asynchronously.
     *
     * @param event the business event to publish
     * @param <T>   the type of business event
     */
    public <T extends BusinessEvent> void publishAsync(T event) {
        if (event == null) return;

        // Simple implementation using a new thread
        // In a production environment, this would use a thread pool or executor service
        new Thread(() -> publish(event)).start();
    }
}