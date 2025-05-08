package com.belman.presentation.core;


import com.belman.business.core.Inject;
import com.belman.business.domain.events.DomainEvent;
import com.belman.business.domain.events.DomainEventPublisher;
import com.belman.data.logging.EmojiLogger;

/**
 * Base class for all services.
 * Provides common functionality and standardizes service implementation.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.business.core.BaseService} instead.
 */
@Deprecated
public abstract class BaseService extends com.belman.business.core.BaseService {

    /**
     * Logger for this service.
     * @deprecated Use the logger from the parent class instead.
     */
    @Deprecated
    protected final EmojiLogger logger = EmojiLogger.getLogger(this.getClass());

    /**
     * Method for injecting services.
     * This method will be overridden by the ServiceLocator to inject services.
     */
    @Inject
    @Override
    protected void injectServices() {
        // This method will be overridden by the ServiceLocator to inject services.
        super.injectServices();
    }

    /**
     * Publishes a domain event.
     * 
     * @param event the event to publish
     * @throws IllegalArgumentException if the event is null
     */
    protected void publishEvent(DomainEvent event) {
        if (event == null) {
            logger.error("Cannot publish null event");
            throw new IllegalArgumentException("Event cannot be null");
        }

        logger.debug("Publishing event: {}", event.getEventType());
        DomainEventPublisher.getInstance().publish(event);
    }

    /**
     * Publishes a domain event asynchronously.
     * 
     * @param event the event to publish
     * @throws IllegalArgumentException if the event is null
     */
    protected void publishEventAsync(DomainEvent event) {
        if (event == null) {
            logger.error("Cannot publish null event asynchronously");
            throw new IllegalArgumentException("Event cannot be null");
        }

        logger.debug("Publishing event asynchronously: {}", event.getEventType());
        DomainEventPublisher.getInstance().publishAsync(event);
    }

    /**
     * Logs a message at the INFO level.
     * 
     * @param message the message to log
     */
    protected void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Logs a message with parameters at the INFO level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    protected void logInfo(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * Logs a message at the DEBUG level.
     * 
     * @param message the message to log
     */
    protected void logDebug(String message) {
        logger.debug(message);
    }

    /**
     * Logs a message with parameters at the DEBUG level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    protected void logDebug(String message, Object... args) {
        logger.debug(message, args);
    }

    /**
     * Logs a message at the WARN level.
     * 
     * @param message the message to log
     */
    protected void logWarn(String message) {
        logger.warn(message);
    }

    /**
     * Logs a message with parameters at the WARN level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    protected void logWarn(String message, Object... args) {
        logger.warn(message, args);
    }

    /**
     * Logs a message at the ERROR level.
     * 
     * @param message the message to log
     */
    protected void logError(String message) {
        logger.error(message);
    }

    /**
     * Logs a message with parameters at the ERROR level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    protected void logError(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * Logs a message with an exception at the ERROR level.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    protected void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
