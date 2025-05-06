package com.belman.presentation.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Base class for all ViewModels in the presentation layer.
 * <p>
 * ViewModels transform domain entities and application DTOs into a form
 * that is easily consumable by the view, providing observable properties
 * that can be bound to UI components. They contain presentation logic but
 * no business logic.
 * <p>
 * This class provides common functionality for all ViewModels, such as
 * loading state tracking and error handling.
 */
public abstract class ViewModel {

    // Common properties for loading state and error handling
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty error = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty();

    /**
     * Indicates whether data is currently being loaded.
     *
     * @return the loading property
     */
    public BooleanProperty loadingProperty() {
        return loading;
    }

    /**
     * Gets the current loading state.
     *
     * @return true if data is being loaded, false otherwise
     */
    public boolean isLoading() {
        return loading.get();
    }

    /**
     * Sets the loading state.
     *
     * @param loading true if data is being loaded, false otherwise
     */
    protected void setLoading(boolean loading) {
        this.loading.set(loading);
    }

    /**
     * Indicates whether an error has occurred.
     *
     * @return the error property
     */
    public BooleanProperty errorProperty() {
        return error;
    }

    /**
     * Gets the current error state.
     *
     * @return true if an error has occurred, false otherwise
     */
    public boolean hasError() {
        return error.get();
    }

    /**
     * Sets the error state.
     *
     * @param error true if an error has occurred, false otherwise
     */
    protected void setError(boolean error) {
        this.error.set(error);
    }

    /**
     * Contains the error message if an error has occurred.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the current error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }

    /**
     * Sets the error message.
     *
     * @param message the error message
     */
    protected void setErrorMessage(String message) {
        this.errorMessage.set(message);
    }

    /**
     * Sets an error state with the given message.
     *
     * @param message the error message
     */
    protected void setError(String message) {
        setError(true);
        setErrorMessage(message);
    }

    /**
     * Clears any error state.
     */
    protected void clearError() {
        setError(false);
        setErrorMessage(null);
    }

    /**
     * Handles an exception by setting the appropriate error state and message.
     *
     * @param e the exception to handle
     */
    protected void handleException(Exception e) {
        setError(e.getMessage());
    }

    /**
     * Initializes the ViewModel, loading any necessary data.
     * This method should be called when the associated view is loaded.
     */
    public abstract void initialize();

    /**
     * Cleans up any resources used by this ViewModel.
     * This method should be called when the associated view is unloaded.
     */
    public void dispose() {
        // Default empty implementation
    }
}