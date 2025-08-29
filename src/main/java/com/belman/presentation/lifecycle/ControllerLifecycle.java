package com.belman.presentation.lifecycle;

/**
 * Interface for controller lifecycle management.
 * This interface abstracts the functionality needed from presentation layer controllers
 * to manage their lifecycle without directly depending on presentation layer classes.
 */
public interface ControllerLifecycle {

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
     * Gets the class name of the controller.
     *
     * @return the simple class name of the controller
     */
    String getControllerName();

    /**
     * Initializes the controller.
     * This method should be called once when the controller is created.
     */
    void initialize();
}
