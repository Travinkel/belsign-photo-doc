package com.belman.backbone.core.events;

import com.belman.backbone.core.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Publisher for domain events.
 * This class is responsible for publishing domain events and notifying registered handlers.
 * It follows the publisher-subscriber pattern.
 */
public class DomainEventPublisher {
    private static DomainEventPublisher instance = new DomainEventPublisher(); // Remove `final` modifier
    private static final Logger logger = Logger.getLogger(DomainEventPublisher.class);

    // Map of event types to handlers
    private final Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers;

    // Executor for asynchronous event handling
    private final ExecutorService executor;

    // Private constructor for singleton
    private DomainEventPublisher() {
        this.handlers = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Gets the singleton instance of the DomainEventPublisher.
     * 
     * @return the DomainEventPublisher instance
     */
    public static DomainEventPublisher getInstance() {
        return instance;
    }

    /**
     * Sets a custom instance of the DomainEventPublisher.
     * This is primarily for testing purposes.
     *
     * @param customInstance the custom instance to set
     */
    public static synchronized void setInstance(DomainEventPublisher customInstance) {
        if (customInstance == null) {
            throw new IllegalArgumentException("Custom instance cannot be null");
        }
        instance = customInstance;
    }

    /**
     * Registers a handler for a specific event type.
     * 
     * @param <T> the type of event
     * @param eventType the class of the event type
     * @param handler the handler to register
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void register(Class<T> eventType, DomainEventHandler<T> handler) {
        logger.debug("Registering handler {} for event type: {}", handler.getClass().getName(), eventType.getName());
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add((DomainEventHandler<? extends DomainEvent>) handler);
    }

    /**
     * Unregisters a handler for a specific event type.
     * 
     * @param <T> the type of event
     * @param eventType the class of the event type
     * @param handler the handler to unregister
     */
    public <T extends DomainEvent> void unregister(Class<T> eventType, DomainEventHandler<T> handler) {
        logger.debug("Unregistering handler {} for event type: {}", handler.getClass().getName(), eventType.getName());
        if (handlers.containsKey(eventType)) {
            boolean removed = handlers.get(eventType).remove(handler);
            if (removed) {
                logger.debug("Handler removed successfully");
            } else {
                logger.debug("Handler was not registered for this event type");
            }

            if (handlers.get(eventType).isEmpty()) {
                logger.debug("No more handlers for event type: {}, removing event type", eventType.getName());
                handlers.remove(eventType);
            }
        } else {
            logger.debug("No handlers registered for event type: {}", eventType.getName());
        }
    }

    /**
     * Publishes an event to all registered handlers.
     * 
     * @param <T> the type of event
     * @param event the event to publish
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void publish(T event) {
        logger.debug("Publishing event: {} (ID: {})", event.getEventType(), event.getEventId());

        if (handlers.containsKey(event.getClass())) {
            List<DomainEventHandler<? extends DomainEvent>> eventHandlers = handlers.get(event.getClass());
            logger.debug("Found {} handlers for event type: {}", eventHandlers.size(), event.getEventType());

            for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
                // Cast is safe because we only register handlers for the correct event type
                DomainEventHandler<T> typedHandler = (DomainEventHandler<T>) handler;
                logger.trace("Handling event with: {}", typedHandler.getClass().getName());
                typedHandler.handle(event);
            }
        } else {
            logger.debug("No handlers found for event type: {}", event.getEventType());
        }
    }

    /**
     * Publishes an event asynchronously to all registered handlers.
     * 
     * @param <T> the type of event
     * @param event the event to publish
     */
    public <T extends DomainEvent> void publishAsync(T event) {
        logger.debug("Publishing event asynchronously: {} (ID: {})", event.getEventType(), event.getEventId());
        executor.submit(() -> publish(event));
    }

    /**
     * Shuts down the executor service.
     * This should be called when the application is shutting down.
     */
    public void shutdown() {
        logger.info("Shutting down DomainEventPublisher executor service");
        if (!executor.isShutdown()) {
            executor.shutdown();
        } else {
            logger.warn("Executor service is already shut down");
        }
    }
}

