package com.belman.domain.exceptions;

import com.belman.domain.services.Logger;
import com.belman.common.logging.EmojiLoggerAdapter;

/**
 * Exception thrown when a user doesn't have the required role to access a resource.
 * This is a business exception since access control is part of the business logic.
 */
public class AccessDeniedException extends BusinessException {

    private static final Logger logger = EmojiLoggerAdapter.getLogger(AccessDeniedException.class);

    /**
     * Creates a new AccessDeniedException with the specified message.
     *
     * @param message the detail message
     */
    public AccessDeniedException(String message) {
        super(message);
        logger.warn("Access denied: {}", message);
    }

    /**
     * Creates a new AccessDeniedException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
        logger.error("Access denied: {}", message, cause);
    }
}
