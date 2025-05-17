package com.belman.presentation.usecases.archive.photo.upload;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for uploading photos to orders.
 * This view allows production workers to take pictures, select order numbers,
 * and upload multiple pictures at once.
 */
public class PhotoUploadView extends BaseView<PhotoUploadViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("Photo Upload");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}