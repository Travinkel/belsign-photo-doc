package com.belman.presentation.usecases.qa.done;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for displaying a completion message after approving or rejecting an order.
 * This is the final screen in the QA flow.
 */
public class QADoneView extends BaseView<QADoneViewModel> {
    /**
     * Constructor for the QA done view.
     */
    public QADoneView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.HOME.button(e -> navigateToHome()));
        appBar.setTitleText("QA Process Completed");
    }

    /**
     * Navigates to the QA dashboard.
     */
    protected void navigateToHome() {
        Router.navigateTo(com.belman.presentation.usecases.qa.dashboard.QADashboardView.class);
    }
}