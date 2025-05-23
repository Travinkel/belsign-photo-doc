package com.belman.presentation.base.role;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.base.LogoutCapable;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * Base class for all admin views in the application.
 * Provides common functionality specific to admin views.
 *
 * @param <T> The type of ViewModel associated with this view
 */
public abstract class AdminBaseView<T extends BaseViewModel<?>> extends BaseView<T> {

    /**
     * Constructor for the admin base view.
     */
    public AdminBaseView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        // Common AppBar setup for admin views
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));

        // Set default title - can be overridden by subclasses
        appBar.setTitleText(getDefaultTitle());

        // Add logout button - common for all admin views
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
        return "Admin";
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
     * Uses the Router for consistent navigation.
     */
    protected void navigateBack() {
        Router.navigateBack();
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
