package com.belman.ui.core;

import com.belman.service.error.ErrorHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Adapter that adapts the UI layer's ErrorHandler to the Service layer's ErrorHandler interface.
 * This allows the UI layer to provide its error handling implementation to the Service layer.
 */
public class UIErrorHandlerAdapter implements ErrorHandler {
    
    private final com.belman.ui.core.ErrorHandler uiErrorHandler;
    
    /**
     * Creates a new UIErrorHandlerAdapter that wraps the given UI ErrorHandler.
     *
     * @param uiErrorHandler the UI ErrorHandler to adapt
     */
    public UIErrorHandlerAdapter(com.belman.ui.core.ErrorHandler uiErrorHandler) {
        this.uiErrorHandler = uiErrorHandler;
    }
    
    @Override
    public void handleExceptionQuietly(Throwable exception, String message) {
        uiErrorHandler.handleExceptionQuietly(exception, message);
    }
    
    @Override
    public void handleException(Throwable exception, String message, boolean showDialog) {
        uiErrorHandler.handleException(exception, message, showDialog);
    }
    
    @Override
    public void handleError(String message) {
        uiErrorHandler.handleError(message);
    }
    
    @Override
    public void handleErrorQuietly(String message) {
        uiErrorHandler.handleErrorQuietly(message);
    }
    
    @Override
    public void executeWithErrorHandling(Runnable operation, String errorMessage) {
        uiErrorHandler.executeWithErrorHandling(operation, errorMessage);
    }
    
    @Override
    public void handleException(Throwable exception, String message) {
        uiErrorHandler.handleException(exception, message);
    }
    
    @Override
    public <T> T executeWithErrorHandling(Supplier<T> operation, String errorMessage, T defaultValue) {
        return uiErrorHandler.executeWithErrorHandling(operation, errorMessage, defaultValue);
    }
    
    @Override
    public <T> void executeAsync(Supplier<T> operation, Consumer<T> onSuccess, String errorMessage) {
        uiErrorHandler.executeAsync(operation, onSuccess, errorMessage);
    }
    
    /**
     * Factory method to create a UIErrorHandlerAdapter that wraps the singleton UI ErrorHandler.
     *
     * @return a new UIErrorHandlerAdapter
     */
    public static UIErrorHandlerAdapter createWithDefaultErrorHandler() {
        return new UIErrorHandlerAdapter(com.belman.ui.core.ErrorHandler.getInstance());
    }
}