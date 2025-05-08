package com.belman.business.core;

import com.gluonhq.charm.glisten.mvc.View;

/**
 * Interface for view lifecycle management.
 * This interface abstracts the functionality needed from presentation layer views
 * to manage their lifecycle without directly depending on presentation layer classes.
 * 
 * @param <T> the type of view model
 * @param <C> the type of controller
 */
public interface ViewLifecycle<T extends ViewModelLifecycle, C extends ControllerLifecycle> {

    /**
     * Gets the underlying View object.
     * 
     * @return the underlying View object
     */
    View getView();

    /**
     * Gets the view model associated with this view.
     * 
     * @return the view model
     */
    T getViewModel();

    /**
     * Gets the controller associated with this view.
     * 
     * @return the controller
     */
    C getController();

    /**
     * Called when the view is shown.
     * This method should handle any initialization needed when the view becomes visible.
     */
    void onViewShown();

    /**
     * Called when the view is hidden.
     * This method should handle any cleanup needed when the view is no longer visible.
     */
    void onViewHidden();

    /**
     * Gets the class name of the view.
     * 
     * @return the simple class name of the view
     */
    String getViewName();
}
