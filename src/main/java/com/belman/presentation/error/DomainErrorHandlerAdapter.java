package com.belman.presentation.error;

import com.belman.domain.services.ErrorHandler;

/**
 * Adapter that implements the domain-level ErrorHandler interface and delegates to the presentation-layer ErrorHandler.
 * This adapter bridges the gap between the domain and presentation layers, allowing the application layer
 * to depend on the domain-level interface while still using the presentation-layer implementation.
 */
public class DomainErrorHandlerAdapter implements ErrorHandler {

    private final com.belman.presentation.error.ErrorHandler presentationErrorHandler;

    /**
     * Creates a new DomainErrorHandlerAdapter with the specified presentation-layer ErrorHandler.
     *
     * @param presentationErrorHandler the presentation-layer ErrorHandler to delegate to
     */
    public DomainErrorHandlerAdapter(com.belman.presentation.error.ErrorHandler presentationErrorHandler) {
        this.presentationErrorHandler = presentationErrorHandler;
    }

    /**
     * Creates a new DomainErrorHandlerAdapter with the default presentation-layer ErrorHandler.
     *
     * @return a new DomainErrorHandlerAdapter
     */
    public static DomainErrorHandlerAdapter createWithDefaultErrorHandler() {
        return new DomainErrorHandlerAdapter(com.belman.presentation.error.ErrorHandler.getInstance());
    }

    @Override
    public void handleException(Throwable exception, String message) {
        presentationErrorHandler.handleException(exception, message);
    }

    @Override
    public void handleExceptionQuietly(Throwable exception, String message) {
        presentationErrorHandler.handleExceptionQuietly(exception, message);
    }

    @Override
    public void handleError(String message) {
        presentationErrorHandler.handleError(message);
    }

    @Override
    public void handleErrorQuietly(String message) {
        presentationErrorHandler.handleErrorQuietly(message);
    }
}