package unit.athomefx.core;


import core.BaseService;
import di.Inject;
import di.ServiceLocator;
import events.DomainEvent;
import events.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class BaseServiceTest {
    
    private TestService testService;
    private DependencyService dependencyService;
    
    @BeforeEach
    void setUp() {
        // Clear any existing services
        ServiceLocator.clear();
        
        // Create and register the dependency service
        dependencyService = new DependencyService();
        ServiceLocator.registerService(DependencyService.class, dependencyService);
        
        // Create and register the test service
        testService = new TestService();
        ServiceLocator.registerService(TestService.class, testService);
        
        // Inject dependencies
        ServiceLocator.injectServices(testService);
    }
    
    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }
    
    @Test
    void testServiceInjection() {
        // Verify that the dependency was injected
        assertNotNull(testService.getDependencyService());
        assertEquals(dependencyService, testService.getDependencyService());
    }
    
    @Test
    void testPublishEvent() {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        DomainEventPublisher.getInstance().register(TestEvent.class, event -> {
            eventHandled.set(true);
        });
        
        // Publish an event
        testService.publishTestEvent();
        
        // Verify that the event was published and handled
        assertTrue(eventHandled.get());
        
        // Clean up
        DomainEventPublisher.getInstance().unregister(TestEvent.class, event -> {});
    }
    
    @Test
    void testLogging() {
        // This test just verifies that the logging methods don't throw exceptions
        testService.testLogging();
    }
    
    /**
     * Test service that extends BaseService.
     */
    static class TestService extends BaseService {
        @Inject
        private DependencyService dependencyService;
        
        public DependencyService getDependencyService() {
            return dependencyService;
        }
        
        public void publishTestEvent() {
            publishEvent(new TestEvent());
        }
        
        public void testLogging() {
            logInfo("Info message");
            logInfo("Info message with parameter: {}", "param");
            logDebug("Debug message");
            logDebug("Debug message with parameter: {}", "param");
            logWarn("Warn message");
            logWarn("Warn message with parameter: {}", "param");
            logError("Error message");
            logError("Error message with parameter: {}", "param");
            logError("Error message with exception", new RuntimeException("Test exception"));
        }
    }
    
    /**
     * Dependency service for testing injection.
     */
    static class DependencyService {
        public String getValue() {
            return "dependency value";
        }
    }
    
    /**
     * Test event for testing event publishing.
     */
    static class TestEvent implements DomainEvent {
        private final UUID eventId = UUID.randomUUID();
        private final Instant timestamp = Instant.now();
        
        @Override
        public UUID getEventId() {
            return eventId;
        }
        
        @Override
        public Instant getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String getEventType() {
            return "TestEvent";
        }
    }
}