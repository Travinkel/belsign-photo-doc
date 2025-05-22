package com.belman.presentation.usecases.qa.assignment;

import com.belman.presentation.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * View for QA users to assign orders to production workers.
 */
public class QAOrderAssignmentView extends BaseView<QAOrderAssignmentViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> {}));
        appBar.setTitleText("QA Order Assignment");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}
