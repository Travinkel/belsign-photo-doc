package com.belman.backbone.core.events;


import com.belman.backbone.core.logging.EmojiLogger;

/**
 * Implementation of the domain event handler functionality.
 * This class provides methods for handling domain events.
 */
public class DomainEventHandlerImplementation {
    private static final EmojiLogger logger = EmojiLogger.getLogger(DomainEventHandlerImplementation.class);

    /**
     * Handles a domain event.
     * 
     * @param event the event to handle
     * @param handlerName the name of the handler
     */
    public static void handleEvent(Object event, String handlerName) {
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
    public static java.util.function.Consumer<Object> createHandler(
            String handlerName, java.util.function.Consumer<Object> handlerFunction) {

        return event -> {
            handleEvent(event, handlerName);
            if (event != null && handlerFunction != null) {
                handlerFunction.accept(event);
            }
        };
    }
}
