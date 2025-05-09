package com.belman.presentation.views.ordergallery;

import com.belman.presentation.core.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for selecting and managing orders.
 * This view allows users to search for orders, filter them by status/date,
 * select an order from the list, view order details, and create new orders.
 */
public class OrderGalleryView extends BaseView<OrderGalleryViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("Order Gallery");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}
