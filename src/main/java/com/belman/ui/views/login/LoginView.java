package com.belman.ui.views.login;

import com.belman.ui.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;

/**
 * View for the login screen.
 */
public class LoginView extends BaseView<LoginViewModel> {
    // This class overrides shouldShowAppBar to hide the app bar on the login screen.

    /**
     * Constructor for the login view.
     * Explicitly specifies the path to the FXML file.
     */
    public LoginView() {
        super();
        System.out.println("LoginView constructor called");
    }

    @Override
    public boolean shouldShowAppBar() {
        return false; // Don't show the app bar on the login screen
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setVisible(false);
    }
}
