package com.belman.service.base;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;

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
     * Method for injecting services.
     * This method will be overridden by the ServiceLocator to inject services.
     */
    @Inject
    protected void injectServices() {
        // This method will be overridden by the ServiceLocator to inject services.
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

    protected abstract LoggerFactory getLoggerFactory();
}
