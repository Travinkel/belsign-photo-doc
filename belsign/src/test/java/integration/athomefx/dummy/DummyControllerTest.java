package integration.athomefx.dummy;

import org.junit.jupiter.api.Test;
import unit.athomefx.dummy.DummyController;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DummyControllerTest {
    @Test
    void testInitializeBinding() {
        DummyController controller = new DummyController();
        assertDoesNotThrow(controller::initializeBinding);
    }
}
