package com.belman.domain.core;

import com.belman.domain.events.DomainEventPublisher;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.shared.DomainEvent;

/**
 * Base class for all domain services.
 * Provides common functionality and standardizes service implementation.
 * This class is designed to be used in the domain layer and does not depend on outer layers.
 */
public abstract class DomainService {

    /**
     * Logger for this service.
     */
    protected final Logger logger;

    /**
     * The logger factory used to create loggers.
     */
    private static LoggerFactory loggerFactory;

    /**
     * Sets the logger factory to be used by all domain services.
     * This method should be called during application initialization.
     * 
     * @param factory the logger factory to use
     */
    public static void setLoggerFactory(LoggerFactory factory) {
        loggerFactory = factory;
    }

    /**
     * Gets the logger factory.
     * 
     * @return the logger factory
     * @throws IllegalStateException if the logger factory has not been set
     */
    protected static LoggerFactory getLoggerFactory() {
        if (loggerFactory == null) {
            throw new IllegalStateException("LoggerFactory has not been set. Call DomainService.setLoggerFactory() during application initialization.");
        }
        return loggerFactory;
    }

    /**
     * Creates a new DomainService with a logger for the concrete service class.
     */
    protected DomainService() {
        this.logger = getLoggerFactory().getLogger(this.getClass());
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
