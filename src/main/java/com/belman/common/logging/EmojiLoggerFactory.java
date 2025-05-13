package com.belman.common.logging;

import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;

/**
 * Implementation of the LoggerFactory interface that creates EmojiLogger instances.
 * This class provides a way to create loggers without directly depending on EmojiLoggerAdapter.
 */
public class EmojiLoggerFactory implements LoggerFactory {

    private static final EmojiLoggerFactory INSTANCE = new EmojiLoggerFactory();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private EmojiLoggerFactory() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Gets the singleton instance of the EmojiLoggerFactory.
     *
     * @return the singleton instance
     */
    public static EmojiLoggerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return EmojiLoggerAdapter.getLogger(clazz);
    }
}