package com.belman.presentation.usecases.login;

import com.belman.presentation.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;

/**
 * View for the login screen.
 * This class extends BaseView and provides the UI for user authentication.
 */
public class LoginView extends BaseView<LoginViewModel> {
    // This class can override shouldShowAppBar to customize the app bar on the login screen.

    @Override
    public boolean shouldShowAppBar() {
        return true; // Show the app bar on the login screen
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Login");
        appBar.setVisible(true);
    }
}