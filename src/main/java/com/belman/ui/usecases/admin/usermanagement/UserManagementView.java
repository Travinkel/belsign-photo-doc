package com.belman.ui.usecases.admin.usermanagement;

import com.belman.ui.base.BaseView;
import com.belman.ui.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for managing users.
 * This view allows administrators to create, edit, and delete user accounts.
 */
public class UserManagementView extends BaseView<UserManagementViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("User Management");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}