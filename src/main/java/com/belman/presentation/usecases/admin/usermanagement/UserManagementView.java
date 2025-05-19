package com.belman.presentation.usecases.admin.usermanagement;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for the User Management screen.
 * This view displays a list of users and allows the admin to add, edit, and delete users.
 */
public class UserManagementView extends BaseView<UserManagementViewModel> {
    /**
     * Constructor for the User Management view.
     */
    public UserManagementView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("User Management");
        appBar.getActionItems().add(MaterialDesignIcon.ADD.button(e -> getViewModel().showAddUserDialog()));
    }

    /**
     * Navigates back to the previous view.
     */
    protected void navigateBack() {
        Router.navigateBack();
    }
}