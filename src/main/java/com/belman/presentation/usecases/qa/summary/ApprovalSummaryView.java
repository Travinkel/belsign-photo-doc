package com.belman.presentation.usecases.qa.summary;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for displaying a summary of the approval or rejection decision.
 * This view shows the result of the QA review process and allows navigation back to the dashboard.
 */
public class ApprovalSummaryView extends BaseView<ApprovalSummaryViewModel> {
    /**
     * Constructor for the approval summary view.
     */
    public ApprovalSummaryView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.HOME.button(e -> navigateToHome()));
        appBar.setTitleText("Approval Summary");
    }

    /**
     * Navigates to the QA dashboard.
     */
    protected void navigateToHome() {
        Router.navigateTo(com.belman.presentation.usecases.qa.dashboard.QADashboardView.class);
    }
}