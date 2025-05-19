package com.belman.presentation.usecases.admin.dashboard;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for the Admin dashboard screen.
 * This view displays admin options and allows navigation to user management.
 */
public class AdminDashboardView extends BaseView<AdminDashboardViewModel> {
    /**
     * Constructor for the Admin dashboard view.
     */
    public AdminDashboardView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("Admin Dashboard");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }

    /**
     * Navigates back to the previous view.
     */
    protected void navigateBack() {
        Router.navigateBack();
    }
}