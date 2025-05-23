package com.belman.presentation.base.feature;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.base.LogoutCapable;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * Base class for all dashboard views in the application.
 * Provides common functionality specific to dashboard views.
 *
 * @param <T> The type of ViewModel associated with this view
 */
public abstract class DashboardBaseView<T extends BaseViewModel<?>> extends BaseView<T> {

    /**
     * Constructor for the dashboard base view.
     */
    public DashboardBaseView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        // Common AppBar setup for dashboard views
        
        // Set default title - can be overridden by subclasses
        appBar.setTitleText(getDefaultTitle());
        
        // Add logout button - common for all dashboard views
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> logout()));
        
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
        return "Dashboard";
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
     * Logs out the current user.
     * Delegates to the ViewModel if it implements LogoutCapable.
     */
    protected void logout() {
        if (getViewModel() != null && getViewModel() instanceof LogoutCapable) {
            ((LogoutCapable) getViewModel()).logout();
        } else {
            System.err.println("Warning: ViewModel does not implement LogoutCapable interface");
        }
    }
}