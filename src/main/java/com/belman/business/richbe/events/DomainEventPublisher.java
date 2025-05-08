package com.belman.business.richbe.events;

import com.belman.business.richbe.services.Logger;

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
public class DomainEventPublisher implements IDomainEventPublisher {
    private static DomainEventPublisher instance = new DomainEventPublisher();
    private Logger logger;

    // Map of event types to handlers
    private final Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers;

    // Executor for asynchronous event handling
    private final ExecutorService executor;

    // Private constructor for singleton
    private DomainEventPublisher() {
        this.handlers = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
        this.logger = null; // Will be set by setLogger method
    }

    /**
     * Sets the logger for this publisher.
     * This method should be called before using the publisher.
     * 
     * @param logger the logger to use
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Safely logs a message at the debug level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private void logDebug(String message, Object... args) {
        if (logger != null) {
            logger.debug(message, args);
        }
    }

    /**
     * Safely logs a message at the info level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private void logInfo(String message, Object... args) {
        if (logger != null) {
            logger.info(message, args);
        }
    }

    /**
     * Safely logs a message at the warn level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private void logWarn(String message, Object... args) {
        if (logger != null) {
            logger.warn(message, args);
        }
    }

    /**
     * Safely logs a message at the trace level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private void logTrace(String message, Object... args) {
        if (logger != null) {
            logger.trace(message, args);
        }
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
        logDebug("Registering handler {} for event type: {}", handler.getClass().getName(), eventType.getName());
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
        logDebug("Unregistering handler {} for event type: {}", handler.getClass().getName(), eventType.getName());
        if (handlers.containsKey(eventType)) {
            boolean removed = handlers.get(eventType).remove(handler);
            if (removed) {
                logDebug("Handler removed successfully");
            } else {
                logDebug("Handler was not registered for this event type");
            }

            if (handlers.get(eventType).isEmpty()) {
                logDebug("No more handlers for event type: {}, removing event type", eventType.getName());
                handlers.remove(eventType);
            }
        } else {
            logDebug("No handlers registered for event type: {}", eventType.getName());
        }
    }

    @Override
    public void publish(DomainEvent event) {
        logDebug("Publishing event: {} (ID: {})", event.getEventType(), event.getEventId());

        if (handlers.containsKey(event.getClass())) {
            List<DomainEventHandler<? extends DomainEvent>> eventHandlers = handlers.get(event.getClass());
            logDebug("Found {} handlers for event type: {}", eventHandlers.size(), event.getEventType());

            for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
                // Cast is safe because we only register handlers for the correct event type
                @SuppressWarnings("unchecked")
                DomainEventHandler<DomainEvent> typedHandler = (DomainEventHandler<DomainEvent>) handler;
                logTrace("Handling event with: {}", typedHandler.getClass().getName());
                typedHandler.handle(event);
            }
        } else {
            logDebug("No handlers found for event type: {}", event.getEventType());
        }
    }

    @Override
    public void publishAll(Iterable<DomainEvent> events) {
        logDebug("Publishing multiple events");
        for (DomainEvent event : events) {
            publish(event);
        }
    }

    @Override
    public <T extends DomainEvent> void publishAsync(T event) {
        logDebug("Publishing event asynchronously: {} (ID: {})", event.getEventType(), event.getEventId());
        executor.submit(() -> publish(event));
    }

    /**
     * Shuts down the executor service.
     * This should be called when the application is shutting down.
     */
    public void shutdown() {
        logInfo("Shutting down DomainEventPublisher executor service");
        if (!executor.isShutdown()) {
            executor.shutdown();
        } else {
            logWarn("Executor service is already shut down");
        }
    }
}