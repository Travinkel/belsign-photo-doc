package com.belman.presentation.usecases.worker.photocube;

import com.belman.presentation.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for displaying the unfolded cube layout for selecting photo templates.
 * This is the second screen in the production worker flow after the assigned order view.
 */
public class PhotoCubeView extends BaseView<PhotoCubeViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> {}));
        appBar.setTitleText("Photo Templates");
    }
}