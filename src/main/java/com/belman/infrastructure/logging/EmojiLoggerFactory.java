package com.belman.infrastructure.logging;

import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;

/**
 * Implementation of LoggerFactory that creates EmojiLoggerAdapter instances.
 * This class is part of the infrastructure layer and provides concrete implementations
 * of the domain layer interfaces.
 */
public class EmojiLoggerFactory implements LoggerFactory {

    /**
     * Singleton instance of the factory.
     */
    private static final EmojiLoggerFactory INSTANCE = new EmojiLoggerFactory();

    /**
     * Gets the singleton instance of the factory.
     * 
     * @return the singleton instance
     */
    public static EmojiLoggerFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private EmojiLoggerFactory() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Gets a logger for the specified class.
     * 
     * @param clazz the class to get a logger for
     * @return a logger instance
     */
    @Override
    public Logger getLogger(Class<?> clazz) {
        return new EmojiLoggerAdapter(clazz);
    }
}