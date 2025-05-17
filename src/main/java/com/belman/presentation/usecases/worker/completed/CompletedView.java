package com.belman.presentation.usecases.worker.completed;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for displaying a completion message after submitting photos.
 * This is the final screen in the production worker flow.
 */
public class CompletedView extends BaseView<CompletedViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.HOME.button(e -> Router.navigateBack()));
        appBar.setTitleText("Order Completed");
    }
}