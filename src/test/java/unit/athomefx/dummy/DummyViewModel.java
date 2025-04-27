package unit.athomefx.dummy;

import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import com.belman.belsign.framework.athomefx.di.Inject;
import com.belman.belsign.framework.athomefx.di.ServiceLocator;
import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import javafx.scene.layout.StackPane;

public class DummyViewModel extends BaseViewModel<DummyViewModel> implements ViewLifecycle {
    @Inject
    private DummyService dummyService;

    public DummyViewModel() {
        ServiceLocator.injectServices(this); // Inject services into the view model
    }

    private boolean shown = false;
    private boolean hidden = false;

    public void inject(DummyService dummyService) {
        this.dummyService = dummyService;
    }

    @Override
    public DummyViewModel getViewModel() {
        return this;
    }

    @Override
    public StackPane getRoot() {
        return new StackPane(); // Dummy root for test
    }

    @Override
    public void onShow() {
        shown = true;
    }

    @Override
    public void onHide() {
        hidden = true;
    }

    public boolean isShown() {
        return shown;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getInjectedServiceMessage() {
        return dummyService.sayHello();
    }
}
