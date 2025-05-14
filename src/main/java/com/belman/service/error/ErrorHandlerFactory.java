package com.belman.service.error;

/**
 * Factory for creating ErrorHandler instances.
 * This class provides access to the application's error handling mechanism.
 */
public class ErrorHandlerFactory {

    private static ErrorHandler instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private ErrorHandlerFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the ErrorHandler instance.
     *
     * @return the ErrorHandler instance
     * @throws IllegalStateException if the ErrorHandler instance has not been set
     */
    public static ErrorHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ErrorHandler instance has not been set. Call setInstance() first.");
        }
        return instance;
    }

    /**
     * Sets the ErrorHandler instance to be used by the factory.
     * This method should be called during application initialization.
     *
     * @param errorHandler the ErrorHandler instance to use
     */
    public static void setInstance(ErrorHandler errorHandler) {
        instance = errorHandler;
    }
}