package com.belman.backbone.core.events;

import com.belman.backbone.core.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for publishing domain events.
 * Provides a more fluent API for publishing events than using the DomainEventPublisher directly.
 */
public class DomainEvents {
    private static final Logger logger = Logger.getLogger(DomainEvents.class);

    // Map to store handlers by consumer and event type
    private static final Map<Class<?>, Map<Consumer<?>, DomainEventHandler<?>>> handlerMap = new ConcurrentHashMap<>();

    /**
     * Publishes a domain event.
     * 
     * @param event the event to publish
     */
    public static void publish(DomainEvent event) {
        if (event == null) {
            logger.warn("Null event provided to publish");
            return;
        }

        logger.debug("Publishing event: {}", event.getEventType());
        DomainEventPublisher.getInstance().publish(event);
    }

    /**
     * Publishes a domain event asynchronously.
     * 
     * @param event the event to publish
     */
    public static void publishAsync(DomainEvent event) {
        if (event == null) {
            logger.warn("Null event provided to publishAsync");
            return;
        }

        logger.debug("Publishing event asynchronously: {}", event.getEventType());
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
            logger.warn("Null event provided to publishIf");
            return;
        }

        if (condition) {
            logger.debug("Condition is true, publishing event: {}", event.getEventType());
            DomainEventPublisher.getInstance().publish(event);
        } else {
            logger.debug("Condition is false, not publishing event: {}", event.getEventType());
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
            logger.warn("Null event provided to publishAsyncIf");
            return;
        }

        if (condition) {
            logger.debug("Condition is true, publishing event asynchronously: {}", event.getEventType());
            DomainEventPublisher.getInstance().publishAsync(event);
        } else {
            logger.debug("Condition is false, not publishing event asynchronously: {}", event.getEventType());
        }
    }

    /**
     * Publishes a domain event if it is not null.
     * 
     * @param eventSupplier a supplier that returns the event to publish
     */
    public static void publishIfPresent(Supplier<DomainEvent> eventSupplier) {
        if (eventSupplier == null) {
            logger.warn("Null event supplier provided to publishIfPresent");
            return;
        }

        DomainEvent event = eventSupplier.get();
        if (event != null) {
            logger.debug("Event is present, publishing event: {}", event.getEventType());
            DomainEventPublisher.getInstance().publish(event);
        } else {
            logger.debug("Event is not present, not publishing");
        }
    }

    /**
     * Publishes a domain event asynchronously if it is not null.
     * 
     * @param eventSupplier a supplier that returns the event to publish
     */
    public static void publishAsyncIfPresent(Supplier<DomainEvent> eventSupplier) {
        if (eventSupplier == null) {
            logger.warn("Null event supplier provided to publishAsyncIfPresent");
            return;
        }

        DomainEvent event = eventSupplier.get();
        if (event != null) {
            logger.debug("Event is present, publishing event asynchronously: {}", event.getEventType());
            DomainEventPublisher.getInstance().publishAsync(event);
        } else {
            logger.debug("Event is not present, not publishing asynchronously");
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

        logger.debug("Registering handler for event type: {}", eventType.getName());

        // Create a DomainEventHandlerImplementation from the Consumer
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

        logger.debug("Unregistering handler for event type: {}", eventType.getName());

        // Get the handler from the map
        Map<Consumer<?>, DomainEventHandler<?>> eventTypeHandlers = handlerMap.get(eventType);
        if (eventTypeHandlers != null) {
            DomainEventHandler<?> eventHandler = eventTypeHandlers.remove(handler);
            if (eventHandler != null) {
                // Unregister the handler from the publisher
                DomainEventPublisher.getInstance().unregister(eventType, (DomainEventHandler<T>) eventHandler);
            } else {
                logger.warn("Handler not found for event type: {}", eventType.getName());
            }
        } else {
            logger.warn("No handlers registered for event type: {}", eventType.getName());
        }
    }
}
