package com.belman.domain.services;

/**
 * Interface for creating Logger instances.
 * This abstraction allows the domain and usecase layers to create loggers
 * without depending on specific logging implementations.
 */
public interface LoggerFactory {

    /**
     * Creates a logger for the specified class.
     *
     * @param clazz the class to create a logger for
     * @return a new logger instance
     */
    Logger getLogger(Class<?> clazz);
}