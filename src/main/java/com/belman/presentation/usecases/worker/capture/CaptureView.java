package com.belman.presentation.usecases.worker.capture;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for capturing photos for a specific template.
 * This is the third screen in the production worker flow after selecting a template.
 */
public class CaptureView extends BaseView<CaptureViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("Take Photo");
    }
}