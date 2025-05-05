package com.belman.application.core;

/**
 * Interface for view model lifecycle management.
 * This interface abstracts the functionality needed from presentation layer view models
 * to manage their lifecycle without directly depending on presentation layer classes.
 */
public interface ViewModelLifecycle {

    /**
     * Called when the associated view is shown.
     * This method should handle any initialization needed when the view becomes visible.
     */
    void onShow();

    /**
     * Called when the associated view is hidden.
     * This method should handle any cleanup needed when the view is no longer visible.
     */
    void onHide();

    /**
     * Gets the class name of the view model.
     * 
     * @return the simple class name of the view model
     */
    String getViewModelName();

    /**
     * Initializes the view model.
     * This method should be called once when the view model is created.
     */
    void initialize();
}
