package com.belman.business.core;

import com.belman.business.richbe.events.DomainEvent;
import com.belman.business.richbe.services.Logger;
import com.belman.business.richbe.services.LoggerFactory;
import com.belman.business.richbe.events.DomainEventPublisher;

/**
 * Base class for all services.
 * Provides common functionality and standardizes service implementation.
 */
public abstract class BaseService {

    /**
     * Logger for this service.
     */
    protected final Logger logger;

    /**
     * Creates a new BaseService with a logger for the concrete service class.
     * 
     * @param loggerFactory the factory to create loggers
     */
    protected BaseService(LoggerFactory loggerFactory) {
        if (loggerFactory == null) {
            throw new IllegalArgumentException("LoggerFactory cannot be null");
        }
        this.logger = loggerFactory.getLogger(this.getClass());
    }

    /**
     * Creates a new BaseService with a logger for the concrete service class.
     * This constructor is deprecated and will be removed in a future version.
     * Use the constructor with LoggerFactory parameter instead.
     */
    @Deprecated
    protected BaseService() {
        // Try to get the LoggerFactory from ServiceLocator
        LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
        this.logger = loggerFactory.getLogger(this.getClass());
    }

    /**
     * Method for injecting services.
     * This method will be overridden by the ServiceLocator to inject services.
     */
    @DependencyInject
    protected void injectServices() {
        // This method will be overridden by the ServiceLocator to inject services.
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
