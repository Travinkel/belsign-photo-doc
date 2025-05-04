package com.belman.domain.services;

/**
 * Factory interface for creating Logger instances.
 * This abstraction allows the domain layer to get logger instances without depending on specific logging implementations.
 */
public interface LoggerFactory {
    /**
     * Gets a logger for the specified class.
     * 
     * @param clazz the class to get a logger for
     * @return a logger instance
     */
    Logger getLogger(Class<?> clazz);
}