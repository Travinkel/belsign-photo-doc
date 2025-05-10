package com.belman.repository.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for logging with emoticons using SLF4J.
 * This class provides methods for logging at different levels with emoticons
 * to make logs more readable and visually distinctive.
 */
public class EmojiLogger {
    
    // Emoticons for different log levels
    private static final String INFO_EMOJI = "ℹ️ ";
    private static final String DEBUG_EMOJI = "🔍 ";
    private static final String WARN_EMOJI = "⚠️ ";
    private static final String ERROR_EMOJI = "❌ ";
    private static final String TRACE_EMOJI = "🔬 ";
    
    // Special emoticons for specific events
    private static final String STARTUP_EMOJI = "🚀 ";
    private static final String SHUTDOWN_EMOJI = "🛑 ";
    private static final String SUCCESS_EMOJI = "✅ ";
    private static final String FAILURE_EMOJI = "❗ ";
    private static final String USER_EMOJI = "👤 ";
    private static final String DATABASE_EMOJI = "💾 ";
    private static final String NETWORK_EMOJI = "🌐 ";
    private static final String FILE_EMOJI = "📁 ";
    private static final String PHOTO_EMOJI = "📷 ";
    private static final String ORDER_EMOJI = "📋 ";
    private static final String REPORT_EMOJI = "📊 ";
    private static final String EMAIL_EMOJI = "📧 ";
    private static final String TIMER_EMOJI = "⏱️ ";
    
    private final Logger logger;
    
    /**
     * Creates a new EmojiLogger for the specified class.
     * 
     * @param clazz the class to log for
     */
    private EmojiLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Gets an EmojiLogger for the specified class.
     * 
     * @param clazz the class to log for
     * @return an EmojiLogger for the specified class
     */
    public static EmojiLogger getLogger(Class<?> clazz) {
        return new EmojiLogger(clazz);
    }
    
    /**
     * Logs a message at the INFO level with the info emoticon.
     * 
     * @param message the message to log
     */
    public void info(String message) {
        logger.info(INFO_EMOJI + message);
    }
    
    /**
     * Logs a message with parameters at the INFO level with the info emoticon.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void info(String message, Object... args) {
        logger.info(INFO_EMOJI + message, args);
    }
    
    /**
     * Logs a message at the DEBUG level with the debug emoticon.
     * 
     * @param message the message to log
     */
    public void debug(String message) {
        logger.debug(DEBUG_EMOJI + message);
    }
    
    /**
     * Logs a message with parameters at the DEBUG level with the debug emoticon.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void debug(String message, Object... args) {
        logger.debug(DEBUG_EMOJI + message, args);
    }
    
    /**
     * Logs a message at the WARN level with the warning emoticon.
     * 
     * @param message the message to log
     */
    public void warn(String message) {
        logger.warn(WARN_EMOJI + message);
    }
    
    /**
     * Logs a message with parameters at the WARN level with the warning emoticon.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void warn(String message, Object... args) {
        logger.warn(WARN_EMOJI + message, args);
    }
    
    /**
     * Logs a message at the ERROR level with the error emoticon.
     * 
     * @param message the message to log
     */
    public void error(String message) {
        logger.error(ERROR_EMOJI + message);
    }
    
    /**
     * Logs a message with parameters at the ERROR level with the error emoticon.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void error(String message, Object... args) {
        logger.error(ERROR_EMOJI + message, args);
    }
    
    /**
     * Logs a message with an exception at the ERROR level with the error emoticon.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    public void error(String message, Throwable throwable) {
        logger.error(ERROR_EMOJI + message, throwable);
    }
    
    /**
     * Logs a message at the TRACE level with the trace emoticon.
     * 
     * @param message the message to log
     */
    public void trace(String message) {
        logger.trace(TRACE_EMOJI + message);
    }
    
    /**
     * Logs a message with parameters at the TRACE level with the trace emoticon.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void trace(String message, Object... args) {
        logger.trace(TRACE_EMOJI + message, args);
    }
    
    /**
     * Logs an application startup message at the INFO level.
     * 
     * @param message the message to log
     */
    public void startup(String message) {
        logger.info(STARTUP_EMOJI + message);
    }
    
    /**
     * Logs an application shutdown message at the INFO level.
     * 
     * @param message the message to log
     */
    public void shutdown(String message) {
        logger.info(SHUTDOWN_EMOJI + message);
    }
    
    /**
     * Logs a success message at the INFO level.
     * 
     * @param message the message to log
     */
    public void success(String message) {
        logger.info(SUCCESS_EMOJI + message);
    }
    
    /**
     * Logs a failure message at the ERROR level.
     * 
     * @param message the message to log
     */
    public void failure(String message) {
        logger.error(FAILURE_EMOJI + message);
    }
    
    /**
     * Logs a user-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void user(String message) {
        logger.info(USER_EMOJI + message);
    }
    
    /**
     * Logs a database-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void database(String message) {
        logger.info(DATABASE_EMOJI + message);
    }
    
    /**
     * Logs a network-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void network(String message) {
        logger.info(NETWORK_EMOJI + message);
    }
    
    /**
     * Logs a file-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void file(String message) {
        logger.info(FILE_EMOJI + message);
    }
    
    /**
     * Logs a photo-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void photo(String message) {
        logger.info(PHOTO_EMOJI + message);
    }
    
    /**
     * Logs an order-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void order(String message) {
        logger.info(ORDER_EMOJI + message);
    }
    
    /**
     * Logs a report-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void report(String message) {
        logger.info(REPORT_EMOJI + message);
    }
    
    /**
     * Logs an email-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void email(String message) {
        logger.info(EMAIL_EMOJI + message);
    }
    
    /**
     * Logs a timing-related message at the INFO level.
     * 
     * @param message the message to log
     */
    public void timer(String message) {
        logger.info(TIMER_EMOJI + message);
    }
}