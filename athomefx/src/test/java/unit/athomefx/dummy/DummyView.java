package unit.athomefx.dummy;


import core.BaseView;
import di.ServiceLocator;

public class DummyView extends BaseView<DummyViewModel> {
    public DummyView() {
        super();
        ServiceLocator.injectServices(this); // Inject services into the view
    }
}

