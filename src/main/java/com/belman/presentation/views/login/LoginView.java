package com.belman.presentation.views.login;

import com.belman.backbone.core.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;

/**
 * View for the login screen.
 */
public class LoginView extends BaseView<LoginViewModel> {
    // This class overrides shouldShowAppBar to hide the app bar on the login screen.

    @Override
    public boolean shouldShowAppBar() {
        return false; // Don't show the app bar on the login screen
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setVisible(false);
    }
}
