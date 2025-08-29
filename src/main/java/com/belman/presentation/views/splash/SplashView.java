package com.belman.presentation.views.splash;

import com.belman.presentation.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;


public class SplashView extends BaseView<SplashViewModel> {
    // This class overrides shouldShowAppBar to hide the app bar on the splash screen.

    @Override
    public boolean shouldShowAppBar() {
        return false; // Don't show the app bar on the splash screen
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setVisible(false);
    }
}
