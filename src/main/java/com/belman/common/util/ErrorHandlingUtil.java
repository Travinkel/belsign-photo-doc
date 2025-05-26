package com.belman.common.util;

import com.belman.domain.services.Logger;
import com.belman.presentation.error.ErrorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class for standardized error handling.
 * Provides methods for safely executing operations and handling exceptions.
 */
public class ErrorHandlingUtil {

    private static final ErrorHandler errorHandler = ErrorHandler.getInstance();

    /**
     * Private constructor to prevent instantiation.
     */
    private ErrorHandlingUtil() {
        // Utility class, do not instantiate
    }

    /**
     * Safely executes an operation and handles exceptions.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param logger the logger to use for logging errors
     * @param <R> the return type of the operation
     * @return the result of the operation, or null if an exception occurs
     */
    public static <R> R executeWithErrorHandling(Supplier<R> operation, String errorMessage, Logger logger) {
        try {
            return operation.get();
        } catch (Exception e) {
            logger.error(errorMessage, e);
            errorHandler.handleException(e, errorMessage);
            return null;
        }
    }

    /**
     * Safely executes an operation that returns a list and handles exceptions.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param logger the logger to use for logging errors
     * @param <R> the type of elements in the list
     * @return the result of the operation, or an empty list if an exception occurs
     */
    public static <R> List<R> executeListWithErrorHandling(Supplier<List<R>> operation, String errorMessage, Logger logger) {
        try {
            return operation.get();
        } catch (Exception e) {
            logger.error(errorMessage, e);
            errorHandler.handleException(e, errorMessage);
            return new ArrayList<>();
        }
    }

    /**
     * Safely executes an operation that returns a boolean and handles exceptions.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param logger the logger to use for logging errors
     * @return the result of the operation, or false if an exception occurs
     */
    public static boolean executeBooleanWithErrorHandling(Supplier<Boolean> operation, String errorMessage, Logger logger) {
        try {
            return operation.get();
        } catch (Exception e) {
            logger.error(errorMessage, e);
            errorHandler.handleException(e, errorMessage);
            return false;
        }
    }

    /**
     * Safely executes an operation with no return value and handles exceptions.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param logger the logger to use for logging errors
     */
    public static void executeVoidWithErrorHandling(Runnable operation, String errorMessage, Logger logger) {
        try {
            operation.run();
        } catch (Exception e) {
            logger.error(errorMessage, e);
            errorHandler.handleException(e, errorMessage);
        }
    }

    /**
     * Safely executes an operation with no return value and handles exceptions quietly.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param logger the logger to use for logging errors
     */
    public static void executeVoidWithQuietErrorHandling(Runnable operation, String errorMessage, Logger logger) {
        try {
            operation.run();
        } catch (Exception e) {
            logger.error(errorMessage, e);
            errorHandler.handleErrorQuietly(errorMessage);
        }
    }
}