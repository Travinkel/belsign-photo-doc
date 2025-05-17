package com.belman.presentation.usecases.archive.admin;

import com.belman.presentation.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;

/**
 * View for the admin management screen.
 * This class is Gluon-aware and uses the backbone framework.
 */
public class AdminView extends BaseView<AdminViewModel> {
    /**
     * Constructor for the admin view.
     * Explicitly specifies the path to the FXML file.
     */
    public AdminView() {
        super();
        System.out.println("AdminView constructor called");
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Admin Management");
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
        com.belman.presentation.navigation.Router.navigateBack();
    }
}