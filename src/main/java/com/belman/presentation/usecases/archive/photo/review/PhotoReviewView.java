package com.belman.presentation.usecases.archive.photo.review;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for reviewing and approving/rejecting photos.
 * This view allows QA engineers to review photos, approve or reject them,
 * and add comments explaining their decisions.
 */
public class PhotoReviewView extends BaseView<PhotoReviewViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("Photo Review");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}