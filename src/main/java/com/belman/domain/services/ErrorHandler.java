package com.belman.domain.services;

/**
 * Interface for error handling services.
 * This interface defines methods for handling errors and exceptions in a domain-agnostic way.
 */
public interface ErrorHandler {

    /**
     * Handles an exception by logging it.
     *
     * @param exception the exception to handle
     * @param message   the error message to log
     */
    void handleException(Throwable exception, String message);

    /**
     * Handles an exception by logging it without displaying it to the user.
     *
     * @param exception the exception to handle
     * @param message   the error message to log
     */
    void handleExceptionQuietly(Throwable exception, String message);

    /**
     * Handles an error by logging it.
     *
     * @param message the error message to log
     */
    void handleError(String message);

    /**
     * Handles an error by logging it without displaying it to the user.
     *
     * @param message the error message to log
     */
    void handleErrorQuietly(String message);
}
