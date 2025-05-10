package com.belman.domain.audit.event;

import com.belman.domain.services.Logger;

/**
 * Implementation of the audit event handler functionality.
 * This class provides methods for handling audit events.
 */
public class AuditHandlerImplementation {
    private final Logger logger;

    /**
     * Creates a new AuditHandlerImplementation with the specified logger.
     *
     * @param logger the logger to use
     */
    public AuditHandlerImplementation(Logger logger) {
        this.logger = logger;
    }

    /**
     * Creates a handler function that logs the event and then applies the specified function.
     *
     * @param handlerName     the name of the handler
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

    /**
     * Handles an audit event.
     *
     * @param event       the event to handle
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
}