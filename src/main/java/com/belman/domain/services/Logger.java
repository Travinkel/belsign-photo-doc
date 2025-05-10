package com.belman.domain.services;

/**
 * Interface for logging services.
 * This abstraction allows the domain layer to log messages without depending on specific logging implementations.
 */
public interface Logger {
    /**
     * Logs a message at the INFO level.
     *
     * @param message the message to log
     */
    void info(String message);

    /**
     * Logs a message with parameters at the INFO level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    void info(String message, Object... args);

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message the message to log
     */
    void debug(String message);

    /**
     * Logs a message with parameters at the DEBUG level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    void debug(String message, Object... args);

    /**
     * Logs a message at the WARN level.
     *
     * @param message the message to log
     */
    void warn(String message);

    /**
     * Logs a message with parameters at the WARN level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    void warn(String message, Object... args);

    /**
     * Logs a message at the ERROR level.
     *
     * @param message the message to log
     */
    void error(String message);

    /**
     * Logs a message with parameters at the ERROR level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    void error(String message, Object... args);

    /**
     * Logs a message with an exception at the ERROR level.
     *
     * @param message   the message to log
     * @param throwable the exception to log
     */
    void error(String message, Throwable throwable);

    /**
     * Logs a message at the TRACE level.
     *
     * @param message the message to log
     */
    void trace(String message);

    /**
     * Logs a message with parameters at the TRACE level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    void trace(String message, Object... args);
}