package com.belman.business.error;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Service layer interface for error handling.
 * This interface defines methods for handling exceptions, logging errors,
 * and executing operations with error handling.
 */
public interface ErrorHandler {
    
    /**
     * Handles an exception by logging it without displaying an error dialog.
     *
     * @param exception the exception to handle
     * @param message   the error message to log
     */
    void handleExceptionQuietly(Throwable exception, String message);
    
    /**
     * Handles an exception by logging it and optionally displaying an error dialog.
     *
     * @param exception  the exception to handle
     * @param message    the error message to display
     * @param showDialog whether to show an error dialog to the user
     */
    void handleException(Throwable exception, String message, boolean showDialog);
    
    /**
     * Handles a runtime error by logging it and displaying an error dialog.
     *
     * @param message the error message to display
     */
    void handleError(String message);
    
    /**
     * Handles a runtime error by logging it without displaying an error dialog.
     *
     * @param message the error message to log
     */
    void handleErrorQuietly(String message);
    
    /**
     * Executes an operation and handles any exceptions that occur.
     *
     * @param operation    the operation to execute
     * @param errorMessage the error message to display if an exception occurs
     */
    void executeWithErrorHandling(Runnable operation, String errorMessage);
    
    /**
     * Handles an exception by logging it and displaying an error dialog.
     *
     * @param exception the exception to handle
     * @param message   the error message to display
     */
    void handleException(Throwable exception, String message);
    
    /**
     * Executes an operation that returns a result and handles any exceptions that occur.
     *
     * @param <T>          the type of the result
     * @param operation    the operation to execute
     * @param errorMessage the error message to display if an exception occurs
     * @param defaultValue the default value to return if an exception occurs
     * @return the result of the operation, or the default value if an exception occurs
     */
    <T> T executeWithErrorHandling(
            Supplier<T> operation,
            String errorMessage,
            T defaultValue);
    
    /**
     * Executes an operation asynchronously and handles any exceptions that occur.
     *
     * @param <T>          the type of the result
     * @param operation    the operation to execute
     * @param onSuccess    the callback to execute if the operation succeeds
     * @param errorMessage the error message to display if an exception occurs
     */
    <T> void executeAsync(
            Supplier<T> operation,
            Consumer<T> onSuccess,
            String errorMessage);
}