package com.belman.presentation.usecases.qa.dashboard;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for the QA dashboard screen.
 * This view displays a list of orders with status PHOTOS_COMPLETE for QA review.
 */
public class QADashboardView extends BaseView<QADashboardViewModel> {
    /**
     * Constructor for the QA dashboard view.
     */
    public QADashboardView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("QA Dashboard");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }

    /**
     * Navigates back to the previous view.
     */
    protected void navigateBack() {
        Router.navigateBack();
    }
}