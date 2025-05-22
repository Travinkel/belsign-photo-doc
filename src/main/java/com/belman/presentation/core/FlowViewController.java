package com.belman.presentation.core;

/**
 * Interface for view controllers that participate in a flow.
 * This interface defines methods for lifecycle events that occur when a view is shown or hidden.
 */
public interface FlowViewController {
    /**
     * Called before the view is shown.
     * This method can be used to initialize the view, load data, or perform other setup tasks.
     */
    void beforeShow();

    /**
     * Called after the view is hidden.
     * This method can be used to clean up resources, save state, or perform other teardown tasks.
     */
    void afterHide();
}
