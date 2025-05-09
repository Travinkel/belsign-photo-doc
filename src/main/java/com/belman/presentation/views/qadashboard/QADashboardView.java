package com.belman.presentation.views.qadashboard;
import com.belman.presentation.core.BaseView;
import com.belman.presentation.navigation.Router;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

public class QADashboardView extends BaseView<QADashboardViewModel> {

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> Router.navigateBack()));
        appBar.setTitleText("QA Dashboard");
        appBar.getActionItems().add(MaterialDesignIcon.POWER_SETTINGS_NEW.button(e -> getViewModel().logout()));
    }
}
