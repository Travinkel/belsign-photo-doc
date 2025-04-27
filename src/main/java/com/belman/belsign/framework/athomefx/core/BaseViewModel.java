package com.belman.belsign.framework.athomefx.core;

/**
 * Base class for all ViewModels.
 * Contains JavaFX observable properties and basic initialization.
 */
public abstract class BaseViewModel {

    /**
     * Called when the ViewModel is about to be shown.
     * Override if needed.
     */
    public void onShow() {
        // Optional to override
    }

    /**
     * Called when the ViewModel is about to be hidden.
     * Override if needed.
     */
    public void onHide() {
        // Optional to override
    }
}
