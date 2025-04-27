package integration.athomefx;

import com.belman.belsign.framework.athomefx.di.ServiceLocator;
import org.junit.jupiter.api.Test;
import unit.athomefx.dummy.DummyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FrameworkIntegrationTest {
    @Test
    void testServiceLocatorRegistration() {
        DummyService dummyService = new DummyService();
        ServiceLocator.registerService(DummyService.class, dummyService);

        DummyService retrievedService = ServiceLocator.getService(DummyService.class);
        assertNotNull(retrievedService);
        assertEquals("Hello, World!", retrievedService.sayHello());
    }
}