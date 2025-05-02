package com.belman.unit.backbone.di;

import com.belman.backbone.core.base.BaseService;
import com.belman.backbone.core.di.Inject;
import com.belman.backbone.core.di.ServiceLocator;
import com.belman.backbone.core.di.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ServiceRegistry class.
 */
public class ServiceRegistryTest {

    // Test service interfaces and implementations
    interface TestService {
        String getData();
    }

    interface AnotherTestService {
        int getValue();
    }

    static class TestServiceImpl implements TestService {
        @Override
        public String getData() {
            return "test data";
        }
    }

    static class MultiInterfaceServiceImpl implements TestService, AnotherTestService {
        @Override
        public String getData() {
            return "multi interface data";
        }

        @Override
        public int getValue() {
            return 42;
        }
    }

    static class TestBaseService extends BaseService {
        @Inject
        private TestService testService;

        public TestService getTestService() {
            return testService;
        }
    }

    @BeforeEach
    void setUp() {
        ServiceLocator.clear();
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    @Test
    void registerService_shouldRegisterUnderClass() {
        // Arrange
        TestServiceImpl service = new TestServiceImpl();

        // Act
        ServiceRegistry.registerService(service);

        // Assert
        TestServiceImpl retrievedService = ServiceLocator.getService(TestServiceImpl.class);
        assertSame(service, retrievedService);
    }

    @Test
    void registerService_shouldRegisterUnderInterfaces() {
        // Arrange
        TestServiceImpl service = new TestServiceImpl();

        // Act
        ServiceRegistry.registerService(service);

        // Assert
        TestService retrievedService = ServiceLocator.getService(TestService.class);
        assertSame(service, retrievedService);
    }

    @Test
    void registerService_withMultipleInterfaces_shouldRegisterUnderAllInterfaces() {
        // Arrange
        MultiInterfaceServiceImpl service = new MultiInterfaceServiceImpl();

        // Act
        ServiceRegistry.registerService(service);

        // Assert
        TestService retrievedTestService = ServiceLocator.getService(TestService.class);
        AnotherTestService retrievedAnotherService = ServiceLocator.getService(AnotherTestService.class);
        
        assertSame(service, retrievedTestService);
        assertSame(service, retrievedAnotherService);
    }

    @Test
    void registerService_withBaseService_shouldInjectServices() {
        // Arrange
        TestServiceImpl testService = new TestServiceImpl();
        TestBaseService baseService = new TestBaseService();

        // Act
        ServiceRegistry.registerService(testService);
        ServiceRegistry.registerService(baseService);

        // Assert
        TestBaseService retrievedBaseService = ServiceLocator.getService(TestBaseService.class);
        assertSame(baseService, retrievedBaseService);
        assertSame(testService, retrievedBaseService.getTestService());
    }

    @Test
    void registerService_withNullService_shouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ServiceRegistry.registerService(null);
        });
    }

    @Test
    void registerAll_withArray_shouldRegisterAllServices() {
        // Arrange
        TestServiceImpl service1 = new TestServiceImpl();
        MultiInterfaceServiceImpl service2 = new MultiInterfaceServiceImpl();

        // Act
        ServiceRegistry.registerAll(service1, service2);

        // Assert
        TestServiceImpl retrievedService1 = ServiceLocator.getService(TestServiceImpl.class);
        MultiInterfaceServiceImpl retrievedService2 = ServiceLocator.getService(MultiInterfaceServiceImpl.class);
        
        assertSame(service1, retrievedService1);
        assertSame(service2, retrievedService2);
    }

    @Test
    void registerAll_withCollection_shouldRegisterAllServices() {
        // Arrange
        TestServiceImpl service1 = new TestServiceImpl();
        MultiInterfaceServiceImpl service2 = new MultiInterfaceServiceImpl();

        // Act
        ServiceRegistry.registerAll(Arrays.asList(service1, service2));

        // Assert
        TestServiceImpl retrievedService1 = ServiceLocator.getService(TestServiceImpl.class);
        MultiInterfaceServiceImpl retrievedService2 = ServiceLocator.getService(MultiInterfaceServiceImpl.class);
        
        assertSame(service1, retrievedService1);
        assertSame(service2, retrievedService2);
    }

    @Test
    void registerAll_withEmptyArray_shouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ServiceRegistry.registerAll();
        });
    }

    @Test
    void registerAll_withNullArray_shouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ServiceRegistry.registerAll((Object[]) null);
        });
    }

    @Test
    void registerAll_withEmptyCollection_shouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ServiceRegistry.registerAll(Collections.emptyList());
        });
    }

    @Test
    void registerAll_withNullCollection_shouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            ServiceRegistry.registerAll((java.util.Collection<?>) null);
        });
    }

    @Test
    void unregisterAll_shouldClearAllServices() {
        // Arrange
        TestServiceImpl service = new TestServiceImpl();
        ServiceRegistry.registerService(service);
        
        // Verify service is registered
        assertSame(service, ServiceLocator.getService(TestServiceImpl.class));
        
        // Act
        ServiceRegistry.unregisterAll();
        
        // Assert
        assertThrows(Exception.class, () -> {
            ServiceLocator.getService(TestServiceImpl.class);
        });
    }
}