package com.belman.common.logging;

import org.slf4j.LoggerFactory;

/**
 * Simple logging facade for the framework.
 * This class provides a simple interface for logging that can be used throughout the framework.
 * <p>
 * This implementation now delegates to SLF4J and adds emoticons to make logs more readable.
 * It is recommended to use this class for all logging in the application.
 */
public class Logger {

    // Emoticons for different log levels
    private static final String INFO_EMOJI = "ℹ️ ";
    private static final String DEBUG_EMOJI = "🔍 ";
    private static final String WARN_EMOJI = "⚠️ ";
    private static final String ERROR_EMOJI = "❌ ";
    private static final String TRACE_EMOJI = "🔬 ";
    private static Level minimumLevel = Level.INFO;
    private final org.slf4j.Logger slf4jLogger;

    /**
     * Creates a new Logger for the specified class.
     *
     * @param clazz the class to log for
     */
    private Logger(Class<?> clazz) {
        this.slf4jLogger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Gets a Logger for the specified class.
     *
     * @param clazz the class to log for
     * @return a Logger for the specified class
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    /**
     * Sets the minimum log level.
     * Messages with a level below this will not be logged.
     * Note: This setting is only used for backward compatibility.
     * SLF4J's own configuration will take precedence.
     *
     * @param level the minimum log level
     */
    public static void setMinimumLevel(Level level) {
        minimumLevel = level;
    }

    /**
     * Logs a message at the TRACE level.
     *
     * @param message the message to log
     */
    public void trace(String message) {
        if (slf4jLogger.isTraceEnabled()) {
            slf4jLogger.trace(TRACE_EMOJI + message);
        }
    }

    /**
     * Logs a message with parameters at the TRACE level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    public void trace(String message, Object... args) {
        if (slf4jLogger.isTraceEnabled()) {
            slf4jLogger.trace(TRACE_EMOJI + message, args);
        }
    }

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message the message to log
     */
    public void debug(String message) {
        if (slf4jLogger.isDebugEnabled()) {
            slf4jLogger.debug(DEBUG_EMOJI + message);
        }
    }

    /**
     * Logs a message with parameters at the DEBUG level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    public void debug(String message, Object... args) {
        if (slf4jLogger.isDebugEnabled()) {
            slf4jLogger.debug(DEBUG_EMOJI + message, args);
        }
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message the message to log
     */
    public void info(String message) {
        if (slf4jLogger.isInfoEnabled()) {
            slf4jLogger.info(INFO_EMOJI + message);
        }
    }

    /**
     * Logs a message with parameters at the INFO level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    public void info(String message, Object... args) {
        if (slf4jLogger.isInfoEnabled()) {
            slf4jLogger.info(INFO_EMOJI + message, args);
        }
    }

    /**
     * Logs a message at the WARN level.
     *
     * @param message the message to log
     */
    public void warn(String message) {
        slf4jLogger.warn(WARN_EMOJI + message);
    }

    /**
     * Logs a message with parameters at the WARN level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    public void warn(String message, Object... args) {
        slf4jLogger.warn(WARN_EMOJI + message, args);
    }

    /**
     * Logs a message at the ERROR level.
     *
     * @param message the message to log
     */
    public void error(String message) {
        slf4jLogger.error(ERROR_EMOJI + message);
    }

    /**
     * Logs a message with parameters at the ERROR level.
     *
     * @param message the message to log
     * @param args    the parameters to the message
     */
    public void error(String message, Object... args) {
        slf4jLogger.error(ERROR_EMOJI + message, args);
    }

    /**
     * Logs a message with an exception at the ERROR level.
     *
     * @param message   the message to log
     * @param throwable the exception to log
     */
    public void error(String message, Throwable throwable) {
        slf4jLogger.error(ERROR_EMOJI + message, throwable);
    }

    /**
     * Log levels.
     */
    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
