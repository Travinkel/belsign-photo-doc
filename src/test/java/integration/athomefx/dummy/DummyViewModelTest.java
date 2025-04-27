package integration.athomefx.dummy;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unit.athomefx.dummy.DummyService;
import unit.athomefx.dummy.DummyViewModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DummyViewModelTest {
    private DummyViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new DummyViewModel();
    }

    @Test
    void testLifecycleMethods() {
        viewModel.onShow();
        assertTrue(viewModel.isShown());

        viewModel.onHide();
        assertTrue(viewModel.isHidden());
    }

    @Test
    void testInjectedService() {
        DummyService dummyService = new DummyService();
        viewModel.inject(dummyService);
        assertEquals("Hello, World!", viewModel.getInjectedServiceMessage());
    }
}
