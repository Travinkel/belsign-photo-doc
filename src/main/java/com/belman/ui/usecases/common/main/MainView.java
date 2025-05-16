package com.belman.ui.usecases.common.main;

import com.belman.ui.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;

/**
 * Main view of the application.
 * This is where the user lands after the splash screen.
 */
public class MainView extends BaseView<MainViewModel> {
    /**
     * Constructor for the main view.
     * Explicitly specifies the path to the FXML file.
     */
    public MainView() {
        super();
        System.out.println("MainView constructor called");
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Role Selection");
        appBar.setNavIcon(getBackButton());
    }

    /**
     * Creates a back button for the app bar.
     * 
     * @return the back button
     */
    private javafx.scene.Node getBackButton() {
        return com.gluonhq.charm.glisten.visual.MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack());
    }

    /**
     * Navigates back to the previous view.
     * Overrides the method in BaseView.
     */
    @Override
    protected void navigateBack() {
        com.belman.ui.navigation.Router.navigateBack();
    }
}