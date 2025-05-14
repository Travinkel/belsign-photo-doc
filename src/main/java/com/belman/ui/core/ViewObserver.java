package com.belman.ui.core;

/**
 * Interface for view observers.
 * This is part of the Observer pattern for view updates.
 */
public interface ViewObserver {
    /**
     * Called when a view is updated.
     *
     * @param event the view event
     */
    void onViewUpdated(ViewEvent event);
}