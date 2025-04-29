package unit.athomefx.di;


import core.BaseService;
import di.Inject;
import di.ServiceLocator;
import di.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceRegistryTest {

    @BeforeEach
    void setUp() {
        // Clear any existing services
        ServiceLocator.clear();
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    @Test
    void testRegisterService() {
        // Create a service
        TestService service = new TestService();

        // Register the service
        ServiceRegistry.registerService(service);

        // Verify that the service was registered under its class
        TestService retrievedService = ServiceLocator.getService(TestService.class);
        assertNotNull(retrievedService);
        assertEquals(service, retrievedService);

        // Verify that the service was registered under its interface
        TestInterface retrievedInterface = ServiceLocator.getService(TestInterface.class);
        assertNotNull(retrievedInterface);
        assertEquals(service, retrievedInterface);
    }

    @Test
    void testRegisterAll() {
        // Create services
        TestService service1 = new TestService();
        AnotherService service2 = new AnotherService();

        // Register the services
        ServiceRegistry.registerAll(service1, service2);

        // Verify that the services were registered
        TestService retrievedService1 = ServiceLocator.getService(TestService.class);
        AnotherService retrievedService2 = ServiceLocator.getService(AnotherService.class);

        assertNotNull(retrievedService1);
        assertNotNull(retrievedService2);
        assertEquals(service1, retrievedService1);
        assertEquals(service2, retrievedService2);
    }

    @Test
    void testRegisterAllCollection() {
        // Create services
        TestService service1 = new TestService();
        AnotherService service2 = new AnotherService();
        List<Object> services = Arrays.asList(service1, service2);

        // Register the services
        ServiceRegistry.registerAll(services);

        // Verify that the services were registered
        TestService retrievedService1 = ServiceLocator.getService(TestService.class);
        AnotherService retrievedService2 = ServiceLocator.getService(AnotherService.class);

        assertNotNull(retrievedService1);
        assertNotNull(retrievedService2);
        assertEquals(service1, retrievedService1);
        assertEquals(service2, retrievedService2);
    }

    @Test
    void testRegisterBaseService() {
        // Create and register the dependency
        Dependency dependency = new Dependency();
        ServiceLocator.registerService(Dependency.class, dependency);

        // Create a service that extends BaseService
        TestBaseService service = new TestBaseService();

        // Register the service
        ServiceRegistry.registerService(service);

        // Verify that the service was registered
        TestBaseService retrievedService = ServiceLocator.getService(TestBaseService.class);
        assertNotNull(retrievedService);
        assertEquals(service, retrievedService);

        // Verify that dependencies were injected
        assertNotNull(retrievedService.getDependency());
        assertEquals(dependency, retrievedService.getDependency());
    }

    @Test
    void testUnregisterAll() {
        // Create and register services
        TestService service1 = new TestService();
        AnotherService service2 = new AnotherService();
        ServiceRegistry.registerAll(service1, service2);

        // Verify that the services were registered
        assertNotNull(ServiceLocator.getService(TestService.class));
        assertNotNull(ServiceLocator.getService(AnotherService.class));

        // Unregister all services
        ServiceRegistry.unregisterAll();

        // Verify that the services were unregistered
        assertThrows(Exception.class, () -> ServiceLocator.getService(TestService.class));
        assertThrows(Exception.class, () -> ServiceLocator.getService(AnotherService.class));
    }

    /**
     * Interface for testing service registration.
     */
    interface TestInterface {
        String getValue();
    }

    /**
     * Service for testing registration.
     */
    static class TestService implements TestInterface {
        @Override
        public String getValue() {
            return "test value";
        }
    }

    /**
     * Another service for testing registration.
     */
    static class AnotherService {
        public String getAnotherValue() {
            return "another value";
        }
    }

    /**
     * Dependency for testing injection.
     */
    static class Dependency {
        public String getValue() {
            return "dependency value";
        }
    }

    /**
     * Service that extends BaseService for testing registration and injection.
     */
    static class TestBaseService extends BaseService {
        @Inject
        private Dependency dependency;

        public Dependency getDependency() {
            return dependency;
        }
    }
}
