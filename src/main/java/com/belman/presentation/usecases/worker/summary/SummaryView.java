package com.belman.presentation.usecases.worker.summary;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for displaying a summary of all photos taken for an order.
 * This is the fourth screen in the production worker flow after capturing all photos.
 */
public class SummaryView extends BaseView<SummaryViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        // Use ViewStackManager directly instead of Router to ensure proper back navigation
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> com.belman.presentation.core.ViewStackManager.getInstance().navigateBack()));
        appBar.setTitleText("Photo Summary");
    }
}
