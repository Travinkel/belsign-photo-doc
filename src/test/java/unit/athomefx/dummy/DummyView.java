package unit.athomefx.dummy;

import com.belman.belsign.framework.athomefx.core.BaseView;
import com.belman.belsign.framework.athomefx.di.ServiceLocator;

public class DummyView extends BaseView<DummyViewModel> {
    public DummyView() {
        super();
        ServiceLocator.injectServices(this); // Inject services into the view
    }
}

