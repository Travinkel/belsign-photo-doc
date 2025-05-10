package com.belman.ui.views.qadashboard;
import com.belman.ui.base.BaseView;
import com.belman.ui.navigation.Router;
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
