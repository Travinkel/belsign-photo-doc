package com.belman.domain.events;

import com.belman.domain.services.Logger;

/**
 * Default implementation of the DomainEventHandlerImplementation interface.
 * This class provides a base implementation for handling domain events
 * and can be extended for more specific event handling.
 *
 * @param <T> the type of event this handler can handle
 */
public class DefaultDomainEventHandler<T extends DomainEvent> implements DomainEventHandler<T> {
    private final Logger logger;
    private final String handlerName;

    /**
     * Creates a new DefaultDomainEventHandler with a default name and the specified logger.
     * 
     * @param logger the logger to use
     */
    public DefaultDomainEventHandler(Logger logger) {
        this.logger = logger;
        this.handlerName = this.getClass().getSimpleName();
    }

    /**
     * Creates a new DefaultDomainEventHandler with the specified name and logger.
     * 
     * @param logger the logger to use
     * @param handlerName the name of this handler
     */
    public DefaultDomainEventHandler(Logger logger, String handlerName) {
        this.logger = logger;
        this.handlerName = handlerName;
    }

    /**
     * Gets the name of this handler.
     * 
     * @return the handler name
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * Handles the specified event.
     * This default implementation logs the event and does nothing else.
     * Subclasses should override this method to provide specific handling logic.
     * 
     * @param event the event to handle
     */
    @Override
    public void handle(T event) {
        if (event == null) {
            logger.warn("{}: Received null event", handlerName);
            return;
        }

        logger.debug("{}: Handling event {} (ID: {})", 
                handlerName, event.getEventType(), event.getEventId());

        // Default implementation does nothing else
        // Subclasses should override this method to provide specific handling logic
    }

    /**
     * Creates a new DefaultDomainEventHandler with the specified handler function.
     * 
     * @param <E> the type of event
     * @param logger the logger to use
     * @param handlerName the name of the handler
     * @param handlerFunction the function to handle events
     * @return a new DefaultDomainEventHandler
     */
    public static <E extends DomainEvent> DefaultDomainEventHandler<E> create(
            Logger logger, String handlerName, java.util.function.Consumer<E> handlerFunction) {
        if (logger == null || handlerName == null || handlerFunction == null) {
            throw new IllegalArgumentException("Logger, handler name, and function cannot be null");
        }

        return new DefaultDomainEventHandler<E>(logger, handlerName) {
            @Override
            public void handle(E event) {
                super.handle(event);
                handlerFunction.accept(event);
            }
        };
    }
}
