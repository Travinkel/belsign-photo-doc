package com.belman.presentation.core;

import com.belman.application.core.ControllerLifecycle;

/**
 * Base class for all controllers in the application.
 * Controllers handle user input and update the view model accordingly.
 * They also receive lifecycle events when the associated view is shown or hidden.
 * 
 * @param <T> The type of ViewModel associated with this controller
 */
public abstract class BaseController<T extends BaseViewModel<?>> implements ControllerLifecycle {
    private T viewModel;

    /**
     * Sets the view model for this controller.
     * 
     * @param viewModel the view model
     */
    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Gets the view model for this controller.
     * 
     * @return the view model
     */
    public T getViewModel() {
        return viewModel;
    }

    /**
     * Initializes the binding between the view model and the view.
     * Override this method in subclasses to set up bindings.
     */
    public void initializeBinding() {
        // Optional: Override in subclasses to bind ViewModel to the view
    }

    /**
     * Called when the associated view is shown.
     * Override this method to perform initialization when the view becomes visible.
     */
    @Override
    public void onShow() {
        // Default implementation does nothing
    }

    /**
     * Called when the associated view is hidden.
     * Override this method to perform cleanup when the view is no longer visible.
     */
    @Override
    public void onHide() {
        // Default implementation does nothing
    }
}
