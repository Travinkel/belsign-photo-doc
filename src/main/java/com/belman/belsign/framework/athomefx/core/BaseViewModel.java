package com.belman.belsign.framework.athomefx.core;

import com.belman.belsign.framework.athomefx.di.Inject;
import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;

/**
 * Base class for all ViewModels.
 * Contains JavaFX observable properties and basic initialization.
 * Manages application state and commands.
 */
public abstract class BaseViewModel<T extends ViewLifecycle> implements ViewLifecycle {
    @Inject
    protected void injectServices() {
        // This method will be overridden by the ServiceLocator to inject services.
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
