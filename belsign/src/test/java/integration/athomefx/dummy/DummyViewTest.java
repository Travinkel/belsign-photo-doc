package integration.athomefx.dummy;

import di.ServiceLocator;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DummyViewTest {

    @BeforeEach
    void setUp() {
        ServiceLocator.clear();
        ServiceLocator.registerService(DummyService.class, new DummyService());
    }

    @Test
    void testViewInitialization() {
        DummyView view = new DummyView();
        assertNotNull(view.getViewModel());
        assertTrue(view.getViewModel() instanceof DummyViewModel);
    }
}
