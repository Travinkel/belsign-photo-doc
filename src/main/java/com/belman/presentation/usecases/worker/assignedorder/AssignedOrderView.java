package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for displaying the worker's currently assigned order.
 * This is the first screen in the production worker flow after login.
 */
public class AssignedOrderView extends BaseView<AssignedOrderViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> {}));
        appBar.setTitleText("Assigned Order");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}