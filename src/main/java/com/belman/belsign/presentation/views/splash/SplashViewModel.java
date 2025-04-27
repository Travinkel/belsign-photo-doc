package com.belman.belsign.presentation.views.splash;

import com.belman.belsign.framework.athomefx.core.BaseView;
import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import com.gluonhq.charm.glisten.mvc.SplashView;
import javafx.scene.layout.StackPane;

public class SplashViewModel extends BaseViewModel<SplashViewModel> implements ViewLifecycle {
    @Override
    public SplashViewModel getViewModel() {
        return this;
    }

    @Override
    public StackPane getRoot() {
        return null;
    }
    // This class is intentionally left empty.
    // It serves as a placeholder for the Splash screen view in the application.
    // The actual implementation will be added later.
}
