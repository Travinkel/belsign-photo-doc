package com.belman.ui.usecases.qa.dashboard;

import com.belman.ui.base.BaseView;
import com.belman.ui.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for the QA dashboard screen.
 * This class is Gluon-aware and uses the backbone framework.
 */
public class QADashboardView extends BaseView<QADashboardViewModel> {
    /**
     * Constructor for the QA dashboard view.
     * Explicitly specifies the path to the FXML file.
     */
    public QADashboardView() {
        super();
        System.out.println("QADashboardView constructor called");
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("QA Dashboard");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }

    /**
     * Navigates back to the previous view.
     * Overrides the method in BaseView.
     */
    @Override
    protected void navigateBack() {
        Router.navigateBack();
    }
}