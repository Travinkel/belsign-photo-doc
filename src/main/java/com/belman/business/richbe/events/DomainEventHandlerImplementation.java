package com.belman.business.richbe.events;

import com.belman.business.richbe.services.Logger;

/**
 * Implementation of the domain event handler functionality.
 * This class provides methods for handling domain events.
 */
public class DomainEventHandlerImplementation {
    private final Logger logger;

    /**
     * Creates a new DomainEventHandlerImplementation with the specified logger.
     * 
     * @param logger the logger to use
     */
    public DomainEventHandlerImplementation(Logger logger) {
        this.logger = logger;
    }

    /**
     * Handles a domain event.
     * 
     * @param event the event to handle
     * @param handlerName the name of the handler
     */
    public void handleEvent(Object event, String handlerName) {
        if (event == null) {
            logger.warn("{}: Received null event", handlerName);
            return;
        }

        logger.debug("{}: Handling event {}", handlerName, event.getClass().getSimpleName());

        // Default implementation does nothing else
        // Specific handlers should be implemented as needed
    }

    /**
     * Creates a handler function that logs the event and then applies the specified function.
     * 
     * @param handlerName the name of the handler
     * @param handlerFunction the function to apply to the event
     * @return a function that logs and handles the event
     */
    public java.util.function.Consumer<Object> createHandler(
            String handlerName, java.util.function.Consumer<Object> handlerFunction) {

        return event -> {
            handleEvent(event, handlerName);
            if (event != null && handlerFunction != null) {
                handlerFunction.accept(event);
            }
        };
    }
}
