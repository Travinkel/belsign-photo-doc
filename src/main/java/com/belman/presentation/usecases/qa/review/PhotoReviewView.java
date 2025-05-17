package com.belman.presentation.usecases.qa.review;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for reviewing photos of an order.
 * This view displays photos for a specific order and allows the QA user to approve or reject them.
 */
public class PhotoReviewView extends BaseView<PhotoReviewViewModel> {
    /**
     * Constructor for the photo review view.
     */
    public PhotoReviewView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        appBar.setTitleText("Photo Review");
    }

    /**
     * Navigates back to the previous view.
     */
    protected void navigateBack() {
        Router.navigateBack();
    }
}