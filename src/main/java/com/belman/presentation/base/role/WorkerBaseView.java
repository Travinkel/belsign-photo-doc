package com.belman.presentation.base.role;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.core.ViewStackManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * Base class for all worker views in the application.
 * Provides common functionality specific to worker views.
 *
 * @param <T> The type of ViewModel associated with this view
 */
public abstract class WorkerBaseView<T extends BaseViewModel<?>> extends BaseView<T> {

    /**
     * Constructor for the worker base view.
     */
    public WorkerBaseView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        // Common AppBar setup for worker views
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        
        // Set default title - can be overridden by subclasses
        appBar.setTitleText(getDefaultTitle());
        
        // Allow subclasses to add additional items
        customizeAppBar(appBar);
    }

    /**
     * Gets the default title for this view.
     * Can be overridden by subclasses to provide a specific title.
     *
     * @return the default title
     */
    protected String getDefaultTitle() {
        return "Production";
    }

    /**
     * Allows subclasses to customize the AppBar beyond the common setup.
     * Default implementation does nothing.
     *
     * @param appBar the AppBar to customize
     */
    protected void customizeAppBar(AppBar appBar) {
        // Default implementation does nothing
    }

    /**
     * Navigates back to the previous view.
     * Uses the ViewStackManager for consistent navigation.
     */
    protected void navigateBack() {
        ViewStackManager.getInstance().navigateBack();
    }
}