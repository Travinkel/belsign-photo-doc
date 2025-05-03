package com.belman.backbone.core.events;

import com.belman.backbone.core.logging.EmojiLogger;

/**
 * Default implementation of the DomainEventHandlerImplementation interface.
 * This class provides a base implementation for handling domain events
 * and can be extended for more specific event handling.
 *
 * @param <T> the type of event this handler can handle
 */
public class DefaultDomainEventHandler<T extends DomainEvent> implements DomainEventHandler<T> {
    private static final EmojiLogger logger = EmojiLogger.getLogger(DefaultDomainEventHandler.class);

    private final String handlerName;

    /**
     * Creates a new DefaultDomainEventHandler with a default name.
     */
    public DefaultDomainEventHandler() {
        this.handlerName = this.getClass().getSimpleName();
    }

    /**
     * Creates a new DefaultDomainEventHandler with the specified name.
     * 
     * @param handlerName the name of this handler
     */
    public DefaultDomainEventHandler(String handlerName) {
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
     * @param handlerName the name of the handler
     * @param handlerFunction the function to handle events
     * @return a new DefaultDomainEventHandler
     */
    public static <E extends DomainEvent> DefaultDomainEventHandler<E> create(
            String handlerName, java.util.function.Consumer<E> handlerFunction) {
        if (handlerName == null || handlerFunction == null) {
            throw new IllegalArgumentException("Handler name and function cannot be null");
        }

        return new DefaultDomainEventHandler<E>(handlerName) {
            @Override
            public void handle(E event) {
                super.handle(event);
                handlerFunction.accept(event);
            }
        };
    }
}
