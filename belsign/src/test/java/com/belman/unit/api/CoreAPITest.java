package dev.stefan.athomefx.core.api;

import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.events.AbstractDomainEvent;
import com.belman.backbone.core.events.DomainEventHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CoreAPI class.
 */
public class CoreAPITest {

    @BeforeEach
    void setUp() {
        // Clear any existing state or services
        CoreAPI.clearServices();
        CoreAPI.clearState();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        CoreAPI.clearServices();
        CoreAPI.clearState();
    }

    @Test
    @DisplayName("Should register and retrieve service")
    void shouldRegisterAndRetrieveService() {
        // Arrange
        TestService service = new TestService();
        
        // Act
        CoreAPI.registerService(TestService.class, service);
        TestService retrievedService = CoreAPI.getService(TestService.class);
        
        // Assert
        assertSame(service, retrievedService, "Retrieved service should be the same instance");
    }

    @Test
    @DisplayName("Should inject services into target object")
    void shouldInjectServicesIntoTargetObject() {
        // Arrange
        TestService service = new TestService();
        CoreAPI.registerService(TestService.class, service);
        TestViewModel viewModel = new TestViewModel();
        
        // Act
        CoreAPI.injectServices(viewModel);
        
        // Assert
        assertSame(service, viewModel.getService(), "Service should be injected into view model");
    }

    @Test
    @DisplayName("Should publish and handle event")
    void shouldPublishAndHandleEvent() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<TestEvent> receivedEvent = new AtomicReference<>();
        
        DomainEventHandler<TestEvent> handler = event -> {
            receivedEvent.set(event);
            latch.countDown();
        };
        
        CoreAPI.registerEventHandler(TestEvent.class, handler);
        TestEvent event = new TestEvent("Test data");
        
        // Act
        CoreAPI.publishEvent(event);
        
        // Assert
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Event should be handled within timeout");
        assertSame(event, receivedEvent.get(), "Handler should receive the correct event");
        
        // Clean up
        CoreAPI.unregisterEventHandler(TestEvent.class, handler);
    }

    @Test
    @DisplayName("Should publish event asynchronously")
    void shouldPublishEventAsynchronously() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        
        DomainEventHandler<TestEvent> handler = event -> {
            handlerCalled.set(true);
            latch.countDown();
        };
        
        CoreAPI.registerEventHandler(TestEvent.class, handler);
        TestEvent event = new TestEvent("Test data");
        
        // Act
        CoreAPI.publishEventAsync(event);
        
        // Assert
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Event should be handled within timeout");
        assertTrue(handlerCalled.get(), "Handler should be called");
        
        // Clean up
        CoreAPI.unregisterEventHandler(TestEvent.class, handler);
    }

    @Test
    @DisplayName("Should set and get state")
    void shouldSetAndGetState() {
        // Arrange
        String key = "testKey";
        String value = "testValue";
        
        // Act
        CoreAPI.setState(key, value);
        String retrievedValue = CoreAPI.getState(key);
        
        // Assert
        assertEquals(value, retrievedValue, "Retrieved value should match the set value");
    }

    @Test
    @DisplayName("Should update state using updater function")
    void shouldUpdateStateUsingUpdaterFunction() {
        // Arrange
        String key = "updateKey";
        CoreAPI.setState(key, 5);
        
        // Act
        CoreAPI.updateState(key, (Integer value) -> value * 2);
        Integer updatedValue = CoreAPI.getState(key);
        
        // Assert
        assertEquals(10, updatedValue, "Value should be updated using the updater function");
    }

    @Test
    @DisplayName("Should notify listener when state changes")
    void shouldNotifyListenerWhenStateChanges() {
        // Arrange
        String key = "listenKey";
        AtomicReference<String> listenerValue = new AtomicReference<>();
        Object owner = new Object();
        
        // Act
        CoreAPI.listenToState(key, owner, (String value) -> listenerValue.set(value));
        CoreAPI.setState(key, "initialValue");
        
        // Assert
        assertEquals("initialValue", listenerValue.get(), "Listener should be notified with the initial value");
        
        // Act again
        CoreAPI.setState(key, "updatedValue");
        
        // Assert again
        assertEquals("updatedValue", listenerValue.get(), "Listener should be notified with the updated value");
        
        // Clean up
        CoreAPI.unlistenToState(key, owner);
    }

    /**
     * Test implementation of BaseViewModel for testing purposes.
     */
    private static class TestViewModel extends BaseViewModel<TestViewModel> {
        private TestService service;
        
        public TestService getService() {
            return service;
        }
        
        public void setService(TestService service) {
            this.service = service;
        }
    }
    
    /**
     * Test service class for testing purposes.
     */
    private static class TestService {
        // Simple test implementation
    }
    
    /**
     * Test event class for testing purposes.
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