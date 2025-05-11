package com.belman.common.exception;

/**
 * Base exception class for all exceptions thrown in the application layer.
 * <p>
 * ApplicationExceptions represent errors that occur during the execution of
 * application-specific business rules or use cases. They provide a way to
 * communicate domain or application errors to the presentation layer.
 * <p>
 * Specific application exceptions should extend this class and provide
 * meaningful error codes and messages to aid in error handling and user feedback.
 */
public class ApplicationException extends Exception {

    private final String errorCode;

    /**
     * Creates a new ApplicationException with the specified message.
     *
     * @param message the detail message
     */
    public ApplicationException(String message) {
        this(message, "GENERIC_ERROR");
    }

    /**
     * Creates a new ApplicationException with the specified message and error code.
     *
     * @param message   the detail message
     * @param errorCode the error code that identifies the type of error
     */
    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates a new ApplicationException with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public ApplicationException(Throwable cause) {
        this(cause.getMessage(), cause, "GENERIC_ERROR");
    }

    /**
     * Creates a new ApplicationException with the specified message, cause and error code.
     *
     * @param message   the detail message
     * @param cause     the cause of this exception
     * @param errorCode the error code that identifies the type of error
     */
    public ApplicationException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}