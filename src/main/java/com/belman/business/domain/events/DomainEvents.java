package com.belman.business.domain.events;

import com.belman.business.domain.services.Logger;
// Import the interfaces from the same package
// No need to import DomainEvent and DomainEventHandler as they are in the same package

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for publishing domain events.
 * Provides a more fluent API for publishing events than using the DomainEventPublisher directly.
 */
public class DomainEvents {
    private static Logger logger;

    /**
     * Sets the logger for this class.
     * This method should be called before using any methods in this class.
     * 
     * @param loggerInstance the logger to use
     */
    public static void setLogger(Logger loggerInstance) {
        logger = loggerInstance;
    }

    /**
     * Safely logs a message at the debug level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private static void logDebug(String message, Object... args) {
        if (logger != null) {
            logger.debug(message, args);
        }
    }

    /**
     * Safely logs a message at the warn level.
     * If the logger is not set, this method does nothing.
     * 
     * @param message the message to log
     * @param args the arguments to the message
     */
    private static void logWarn(String message, Object... args) {
        if (logger != null) {
            logger.warn(message, args);
        }
    }

    // Map to store handlers by consumer and event type
    private static final Map<Class<?>, Map<Consumer<?>, DomainEventHandler<?>>> handlerMap = new ConcurrentHashMap<>();

    /**
     * Publishes a domain event.
     * 
     * @param event the event to publish
     */
    public static void publish(DomainEvent event) {
        if (event == null) {
            logWarn("Null event provided to publish");
            return;
        }

        logDebug("Publishing event: {}", event.getEventType());
        DomainEventPublisher.getInstance().publish(event);
    }

    /**
     * Publishes a domain event asynchronously.
     * 
     * @param event the event to publish
     */
    public static void publishAsync(DomainEvent event) {
        if (event == null) {
            logWarn("Null event provided to publishAsync");
            return;
        }

        logDebug("Publishing event asynchronously: {}", event.getEventType());
        DomainEventPublisher.getInstance().publishAsync(event);
    }

    /**
     * Publishes a domain event if a condition is true.
     * 
     * @param condition the condition to check
     * @param event the event to publish if the condition is true
     */
    public static void publishIf(boolean condition, DomainEvent event) {
        if (event == null) {
            logWarn("Null event provided to publishIf");
            return;
        }

        if (condition) {
            logDebug("Condition is true, publishing event: {}", event.getEventType());
            DomainEventPublisher.getInstance().publish(event);
        } else {
            logDebug("Condition is false, not publishing event: {}", event.getEventType());
        }
    }

    /**
     * Publishes a domain event asynchronously if a condition is true.
     * 
     * @param condition the condition to check
     * @param event the event to publish if the condition is true
     */
    public static void publishAsyncIf(boolean condition, DomainEvent event) {
        if (event == null) {
            logWarn("Null event provided to publishAsyncIf");
            return;
        }

        if (condition) {
            logDebug("Condition is true, publishing event asynchronously: {}", event.getEventType());
            DomainEventPublisher.getInstance().publishAsync(event);
        } else {
            logDebug("Condition is false, not publishing event asynchronously: {}", event.getEventType());
        }
    }

    /**
     * Publishes a domain event if it is not null.
     * 
     * @param eventSupplier a supplier that returns the event to publish
     */
    public static void publishIfPresent(Supplier<DomainEvent> eventSupplier) {
        if (eventSupplier == null) {
            logWarn("Null event supplier provided to publishIfPresent");
            return;
        }

        DomainEvent event = eventSupplier.get();
        if (event != null) {
            logDebug("Event is present, publishing event: {}", event.getEventType());
            DomainEventPublisher.getInstance().publish(event);
        } else {
            logDebug("Event is not present, not publishing");
        }
    }

    /**
     * Publishes a domain event asynchronously if it is not null.
     * 
     * @param eventSupplier a supplier that returns the event to publish
     */
    public static void publishAsyncIfPresent(Supplier<DomainEvent> eventSupplier) {
        if (eventSupplier == null) {
            logWarn("Null event supplier provided to publishAsyncIfPresent");
            return;
        }

        DomainEvent event = eventSupplier.get();
        if (event != null) {
            logDebug("Event is present, publishing event asynchronously: {}", event.getEventType());
            DomainEventPublisher.getInstance().publishAsync(event);
        } else {
            logDebug("Event is not present, not publishing asynchronously");
        }
    }

    /**
     * Registers a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to register
     * @param <T> the type of event
     */
    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void on(Class<T> eventType, Consumer<T> handler) {
        if (eventType == null || handler == null) {
            throw new IllegalArgumentException("Event type and handler cannot be null");
        }

        logDebug("Registering handler for event type: {}", eventType.getName());

        // Create a DomainEventHandler from the Consumer
        DomainEventHandler<T> eventHandler = handler::accept;

        handlerMap.computeIfAbsent(eventType, k -> new ConcurrentHashMap<>())
                  .put(handler, eventHandler);

        // Register the handler with the publisher
        DomainEventPublisher.getInstance().register(eventType, eventHandler);
    }

    /**
     * Unregisters a handler for a specific event type.
     * 
     * @param eventType the class of the event type
     * @param handler the handler to unregister
     * @param <T> the type of event
     */
    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void off(Class<T> eventType, Consumer<T> handler) {
        if (eventType == null || handler == null) {
            throw new IllegalArgumentException("Event type and handler cannot be null");
        }

        logDebug("Unregistering handler for event type: {}", eventType.getName());

        // Get the handler from the map
        Map<Consumer<?>, DomainEventHandler<?>> eventTypeHandlers = handlerMap.get(eventType);
        if (eventTypeHandlers != null) {
            DomainEventHandler<?> eventHandler = eventTypeHandlers.remove(handler);
            if (eventHandler != null) {
                // Unregister the handler from the publisher
                DomainEventPublisher.getInstance().unregister(eventType, (DomainEventHandler<T>) eventHandler);
            } else {
                logWarn("Handler not found for event type: {}", eventType.getName());
            }
        } else {
            logWarn("No handlers registered for event type: {}", eventType.getName());
        }
    }
}
