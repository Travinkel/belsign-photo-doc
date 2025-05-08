package com.belman.data.logging;

import com.belman.business.domain.services.Logger;

/**
 * Adapter that implements the domain Logger interface using EmojiLogger.
 * This allows the domain layer to use logging without depending on the specific EmojiLogger implementation.
 */
public class EmojiLoggerAdapter implements Logger {
    private final EmojiLogger emojiLogger;

    /**
     * Creates a new EmojiLoggerAdapter for the specified class.
     * 
     * @param clazz the class to create a logger for
     */
    public EmojiLoggerAdapter(Class<?> clazz) {
        this.emojiLogger = EmojiLogger.getLogger(clazz);
    }

    /**
     * Factory method to create a logger for the specified class.
     * 
     * @param clazz the class to create a logger for
     * @return a new logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return new EmojiLoggerAdapter(clazz);
    }

    @Override
    public void info(String message) {
        emojiLogger.info(message);
    }

    @Override
    public void info(String message, Object... args) {
        emojiLogger.info(message, args);
    }

    @Override
    public void debug(String message) {
        emojiLogger.debug(message);
    }

    @Override
    public void debug(String message, Object... args) {
        emojiLogger.debug(message, args);
    }

    @Override
    public void warn(String message) {
        emojiLogger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        emojiLogger.warn(message, args);
    }

    @Override
    public void error(String message) {
        emojiLogger.error(message);
    }

    @Override
    public void error(String message, Object... args) {
        emojiLogger.error(message, args);
    }

    @Override
    public void error(String message, Throwable throwable) {
        emojiLogger.error(message, throwable);
    }

    @Override
    public void trace(String message) {
        emojiLogger.trace(message);
    }

    @Override
    public void trace(String message, Object... args) {
        emojiLogger.trace(message, args);
    }
}