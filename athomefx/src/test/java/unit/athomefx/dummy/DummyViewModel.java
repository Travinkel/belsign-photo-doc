package unit.athomefx.dummy;


import core.BaseViewModel;
import di.Inject;
import di.ServiceLocator;

public class DummyViewModel extends BaseViewModel<DummyViewModel> {
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
    public void onShow() {
        shown = true;
        super.onShow();
    }

    @Override
    public void onHide() {
        hidden = true;
        super.onHide();
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
