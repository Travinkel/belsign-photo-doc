package com.belman.belsign.framework.athomefx.core;

/**
 * Base Controller linked to a ViewModel.
 *
 * @param <T> the ViewModel type
 */
public abstract class BaseController<T extends BaseViewModel> {

    protected T viewModel;

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Called automatically after the View and ViewModel are bound.
     * Override to bind properties, listeners, etc.
     */
    public void initializeBinding() {
        // Optional override
    }
}
