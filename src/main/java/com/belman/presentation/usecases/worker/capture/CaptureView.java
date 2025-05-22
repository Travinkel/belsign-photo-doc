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
        // Use ViewStackManager directly instead of Router to ensure proper back navigation
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> com.belman.presentation.core.ViewStackManager.getInstance().navigateBack()));
        appBar.setTitleText("Take Photo");
    }
}
