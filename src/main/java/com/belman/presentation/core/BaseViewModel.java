package com.belman.presentation.core;

import com.belman.application.core.ViewModelLifecycle;

/**
 * Base class for all ViewModels.
 * Contains observable properties and basic initialization.
 * Manages application state and commands.
 */
public abstract class BaseViewModel<T> implements ViewModelLifecycle {
    protected void injectServices() {
        // This method will be overridden by the ServiceLocator to inject services.
    }

    /**
     * Gets the class name of the view model.
     * 
     * @return the simple class name of the view model
     */
    @Override
    public String getViewModelName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Initializes the view model.
     * This method is called once when the view model is created.
     */
    @Override
    public void initialize() {
        // Default implementation does nothing
    }

    /**
     * Called when the ViewModel is about to be shown.
     * Override if needed.
     */
    @Override
    public void onShow() {
        // Optional to override
    }

    /**
     * Called when the ViewModel is about to be hidden.
     * Override if needed.
     */
    @Override
    public void onHide() {
        // Optional to override
    }
}
