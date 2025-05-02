package com.belman.unit.backbone.di;

import com.belman.backbone.core.di.Inject;
import com.belman.backbone.core.di.ServiceLocator;
import com.belman.backbone.core.exceptions.ServiceInjectionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ServiceLocator class.
 */
public class ServiceLocatorTest {

    // Test service interfaces and implementations
    interface TestService {
        String getData();
    }

    static class TestServiceImpl implements TestService {
        @Override
        public String getData() {
            return "test data";
        }
    }

    static class ServiceConsumer {
        @Inject
        private TestService testService;

        public TestService getTestService() {
            return testService;
        }
    }

    static class MethodInjectionConsumer {
        private boolean methodCalled = false;

        @Inject
        private void init() {
            methodCalled = true;
        }

        public boolean isMethodCalled() {
            return methodCalled;
        }
    }

    static class SetterInjectionConsumer {
        private TestService testService;

        public void setTestService(TestService testService) {
            this.testService = testService;
        }

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
    void registerService_shouldRegisterService() {
        // Arrange
        TestService testService = new TestServiceImpl();

        // Act
        ServiceLocator.registerService(TestService.class, testService);

        // Assert
        TestService retrievedService = ServiceLocator.getService(TestService.class);
        assertSame(testService, retrievedService);
    }

    @Test
    void registerService_withNullInstance_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ServiceLocator.registerService(TestService.class, null);
        });
        
        assertTrue(exception.getMessage().contains("Service instance cannot be null"));
    }

    @Test
    void registerService_duplicateRegistration_shouldThrowException() {
        // Arrange
        TestService testService1 = new TestServiceImpl();
        TestService testService2 = new TestServiceImpl();
        
        // Act
        ServiceLocator.registerService(TestService.class, testService1);
        
        // Assert
        Exception exception = assertThrows(ServiceInjectionException.class, () -> {
            ServiceLocator.registerService(TestService.class, testService2);
        });
        
        assertTrue(exception.getMessage().contains("Service already registered"));
    }

    @Test
    void injectServices_shouldInjectFieldServices() {
        // Arrange
        TestService testService = new TestServiceImpl();
        ServiceLocator.registerService(TestService.class, testService);
        ServiceConsumer consumer = new ServiceConsumer();
        
        // Act
        ServiceLocator.injectServices(consumer);
        
        // Assert
        assertSame(testService, consumer.getTestService());
    }

    @Test
    void injectServices_withMissingService_shouldThrowException() {
        // Arrange
        ServiceConsumer consumer = new ServiceConsumer();
        
        // Act & Assert
        Exception exception = assertThrows(ServiceInjectionException.class, () -> {
            ServiceLocator.injectServices(consumer);
        });
        
        assertTrue(exception.getMessage().contains("No service registered for"));
    }

    @Test
    void injectServices_shouldInjectMethodServices() {
        // Arrange
        MethodInjectionConsumer consumer = new MethodInjectionConsumer();
        
        // Act
        ServiceLocator.injectServices(consumer);
        
        // Assert
        assertTrue(consumer.isMethodCalled());
    }

    @Test
    void injectServices_shouldInjectSetterServices() {
        // Arrange
        TestService testService = new TestServiceImpl();
        ServiceLocator.registerService(TestService.class, testService);
        SetterInjectionConsumer consumer = new SetterInjectionConsumer();
        
        // Act
        ServiceLocator.injectServices(consumer);
        
        // Assert
        assertSame(testService, consumer.getTestService());
    }

    @Test
    void getService_withMissingService_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(ServiceInjectionException.class, () -> {
            ServiceLocator.getService(TestService.class);
        });
        
        assertTrue(exception.getMessage().contains("No service registered for"));
    }

    @Test
    void clear_shouldRemoveAllServices() {
        // Arrange
        TestService testService = new TestServiceImpl();
        ServiceLocator.registerService(TestService.class, testService);
        
        // Act
        ServiceLocator.clear();
        
        // Assert
        Exception exception = assertThrows(ServiceInjectionException.class, () -> {
            ServiceLocator.getService(TestService.class);
        });
        
        assertTrue(exception.getMessage().contains("No service registered for"));
    }
}