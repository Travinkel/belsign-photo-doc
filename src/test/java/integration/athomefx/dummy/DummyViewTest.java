package integration.athomefx.dummy;

import org.junit.jupiter.api.Test;
import unit.athomefx.dummy.DummyView;
import unit.athomefx.dummy.DummyViewModel;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DummyViewTest {
    @Test
    void testViewInitialization() {
        DummyView view = new DummyView();
        assertNotNull(view.getViewModel());
        assertTrue(view.getViewModel() instanceof DummyViewModel);
    }
}
