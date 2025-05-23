package com.belman.domain.core;

import com.belman.domain.events.DomainEvent;
import com.belman.domain.events.DomainEventPublisher;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;

/**
 * Base class for all domain services.
 * Provides common functionality and standardizes service implementation.
 * This class is designed to be used in the domain layer and does not depend on outer layers.
 */
public abstract class DomainService implements IDomainService {

    /**
     * The logger for this service.
     */
    protected final Logger logger;

    /**
     * Creates a new DomainService with a logger for the concrete service class.
     */
    protected DomainService() {
        this.logger = getLoggerFactory().getLogger(this.getClass());
    }

    /**
     * Gets the logger factory to use for creating loggers.
     * <p>
     * This method must be implemented by subclasses to provide a LoggerFactory.
     * The factory is typically injected by the infrastructure layer.
     *
     * @return the LoggerFactory to use for creating loggers
     */
    protected abstract LoggerFactory getLoggerFactory();

    /**
     * Gets a string representation of the service's state for logging purposes.
     * <p>
     * By default, this method returns an empty string.
     * Subclasses may override this method to provide a different representation.
     *
     * @return a string representation of the service's state
     */
    protected String getStateForLogging() {
        return "";
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
     * @param args    the parameters to the message
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
     * @param args    the parameters to the message
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
     * @param args    the parameters to the message
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
     * @param args    the parameters to the message
     */
    protected void logError(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * Logs a message with an exception at the ERROR level.
     *
     * @param message   the message to log
     * @param throwable the exception to log
     */
    protected void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
