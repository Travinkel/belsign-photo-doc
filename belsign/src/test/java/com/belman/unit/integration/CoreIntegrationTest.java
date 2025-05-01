package dev.stefan.athomefx.core.integration;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.base.BaseService;
import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.lifecycle.ViewLifecycle;
import dev.stefan.athomefx.core.di.ServiceLocator;
import com.belman.backbone.core.events.AbstractDomainEvent;
import com.belman.backbone.core.events.DomainEventHandler;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.backbone.core.state.StateStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the core module.
 * These tests verify that the core components work together correctly.
 */
public class CoreIntegrationTest {

    private TestViewModel viewModel;
    private TestController controller;
    private TestService service;
    private StateStore stateStore;
    private DomainEventPublisher publisher;

    @BeforeEach
    void setUp() {
        // Get instances of singletons
        stateStore = StateStore.getInstance();
        publisher = DomainEventPublisher.getInstance();

        // Clear state store
        stateStore.clear();

        // Create test components
        viewModel = new TestViewModel();
        controller = new TestController();
        service = new TestService();

        // Register service with ServiceLocator
        ServiceLocator.registerService(TestService.class, service);

        // Set up controller with view model
        controller.setViewModel(viewModel);
        controller.initializeBinding();
    }

    private DomainEventHandler<TestEvent> testEventHandler;

    @AfterEach
    void tearDown() {
        // Clear all registered services
        ServiceLocator.clear();

        // Unregister event handlers
        if (testEventHandler != null) {
            publisher.unregister(TestEvent.class, testEventHandler);
        }
    }

    @Test
    @DisplayName("Should propagate state changes from service to view model")
    void shouldPropagateStateChangesFromServiceToViewModel() {
        // Arrange
        String testKey = "testKey";
        String testValue = "testValue";

        // Set up view model to listen for state changes
        viewModel.listenForStateChanges(testKey);

        // Act
        service.updateState(testKey, testValue);

        // Assert
        assertEquals(testValue, viewModel.getStateValue(), "View model should receive state changes");
    }

    @Test
    @DisplayName("Should handle domain events between service and view model")
    void shouldHandleDomainEventsBetweenServiceAndViewModel() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean eventHandled = new AtomicBoolean(false);

        // Register event handler
        testEventHandler = event -> {
            eventHandled.set(true);
            latch.countDown();
        };
        publisher.register(TestEvent.class, testEventHandler);

        // Ensure service is injected into view model
        ServiceLocator.injectServices(viewModel);

        // Act
        viewModel.triggerEvent();

        // Assert
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Event should be handled within timeout");
        assertTrue(eventHandled.get(), "Event should be handled");
    }

    @Test
    @DisplayName("Should inject services into view model")
    void shouldInjectServicesIntoViewModel() {
        // Act
        ServiceLocator.injectServices(viewModel);

        // Assert
        assertNotNull(viewModel.getService(), "Service should be injected into view model");
        assertSame(service, viewModel.getService(), "Injected service should be the same instance");
    }

    /**
     * Test implementation of BaseViewModel for testing purposes.
     */
    private static class TestViewModel extends BaseViewModel<ViewLifecycle> {
        private TestService service;
        private String stateValue;

        public void listenForStateChanges(String key) {
            StateStore.getInstance().listen(key, this, value -> stateValue = (String) value);
        }

        public void triggerEvent() {
            if (service != null) {
                service.publishTestEvent();
            }
        }

        public String getStateValue() {
            return stateValue;
        }

        public TestService getService() {
            return service;
        }

        // This method will be called by ServiceLocator.injectServices
        public void setService(TestService service) {
            this.service = service;
        }
    }

    /**
     * Test implementation of BaseController for testing purposes.
     */
    private static class TestController extends BaseController<TestViewModel> {
        // Simple test implementation
    }

    /**
     * Test implementation of BaseService for testing purposes.
     */
    private static class TestService extends BaseService {
        public void updateState(String key, String value) {
            StateStore.getInstance().set(key, value);
        }

        public void publishTestEvent() {
            publishEvent(new TestEvent("Test event data"));
        }
    }

    /**
     * Test implementation of AbstractDomainEvent for testing purposes.
     */
    private static class TestEvent extends AbstractDomainEvent {
        private final String data;

        public TestEvent(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }
}
