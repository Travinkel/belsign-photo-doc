package com.belman.unit.backbone.core.events;

import com.belman.domain.shared.DomainEvent;
import com.belman.domain.shared.DomainEventHandler;
import com.belman.domain.events.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DomainEventPublisher class.
 * These tests verify that the publisher correctly dispatches events to registered handlers
 * without using reflection or annotation scanning.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DomainEventPublisherTest {

    private DomainEventPublisher publisher;

    @BeforeEach
    void setUp() throws Exception {
        // Get the singleton instance
        publisher = DomainEventPublisher.getInstance();

        // Reset the state of the singleton instance using reflection
        // This is necessary because we can't create a new instance directly
        resetDomainEventPublisher();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after each test
        resetDomainEventPublisher();
    }

    /**
     * Resets the state of the DomainEventPublisher singleton instance using reflection.
     * This is necessary because we can't create a new instance directly.
     */
    private void resetDomainEventPublisher() throws Exception {
        // Get the handlers field
        Field handlersField = DomainEventPublisher.class.getDeclaredField("handlers");
        handlersField.setAccessible(true);

        // Clear the handlers map
        Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers = 
            (Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>>) handlersField.get(publisher);
        handlers.clear();

        // Get the executor field
        Field executorField = DomainEventPublisher.class.getDeclaredField("executor");
        executorField.setAccessible(true);

        // Get the current executor
        ExecutorService currentExecutor = (ExecutorService) executorField.get(publisher);

        // Shutdown the current executor if it's not already shut down
        if (currentExecutor != null && !currentExecutor.isShutdown()) {
            currentExecutor.shutdown();
        }

        // Create a new executor
        ExecutorService newExecutor = Executors.newCachedThreadPool();
        executorField.set(publisher, newExecutor);
    }

    /**
     * Test event class for testing the publisher.
     */
    static class TestEvent implements DomainEvent {
        private final UUID eventId = UUID.randomUUID();
        private final Instant timestamp = Instant.now();
        private final String message;

        TestEvent(String message) {
            this.message = message;
        }

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

        public String getMessage() {
            return message;
        }
    }

    /**
     * Another test event class for testing event type specificity.
     */
    static class AnotherTestEvent implements DomainEvent {
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
            return "AnotherTestEvent";
        }
    }

    @Test
    void testPublishDispatchesToRegisteredHandler() {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        TestEvent testEvent = new TestEvent("Hello, World!");

        DomainEventHandler<TestEvent> handler = event -> {
            handlerCalled.set(true);
            assertEquals("Hello, World!", event.getMessage());
        };

        // Register the handler
        publisher.register(TestEvent.class, handler);

        // Act
        publisher.publish(testEvent);

        // Assert
        assertTrue(handlerCalled.get(), "Handler should have been called");
    }

    @Test
    void testHandlerOnlyReceivesEventsOfRegisteredType() {
        // Arrange
        AtomicBoolean testEventHandlerCalled = new AtomicBoolean(false);
        AtomicBoolean anotherTestEventHandlerCalled = new AtomicBoolean(false);

        TestEvent testEvent = new TestEvent("Test");
        AnotherTestEvent anotherTestEvent = new AnotherTestEvent();

        DomainEventHandler<TestEvent> testEventHandler = event -> testEventHandlerCalled.set(true);
        DomainEventHandler<AnotherTestEvent> anotherTestEventHandler = event -> anotherTestEventHandlerCalled.set(true);

        // Register handlers
        publisher.register(TestEvent.class, testEventHandler);
        publisher.register(AnotherTestEvent.class, anotherTestEventHandler);

        // Act
        publisher.publish(testEvent);

        // Assert
        assertTrue(testEventHandlerCalled.get(), "TestEvent handler should have been called");
        assertFalse(anotherTestEventHandlerCalled.get(), "AnotherTestEvent handler should not have been called");

        // Reset and test the other event type
        testEventHandlerCalled.set(false);
        publisher.publish(anotherTestEvent);

        assertFalse(testEventHandlerCalled.get(), "TestEvent handler should not have been called");
        assertTrue(anotherTestEventHandlerCalled.get(), "AnotherTestEvent handler should have been called");
    }

    @Test
    void testUnregisterRemovesHandler() {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        TestEvent testEvent = new TestEvent("Test");

        DomainEventHandler<TestEvent> handler = event -> handlerCalled.set(true);

        // Register and then unregister the handler
        publisher.register(TestEvent.class, handler);
        publisher.unregister(TestEvent.class, handler);

        // Act
        publisher.publish(testEvent);

        // Assert
        assertFalse(handlerCalled.get(), "Handler should not have been called after unregistering");
    }

    @Test
    void testMultipleHandlersForSameEventType() {
        // Arrange
        AtomicBoolean firstHandlerCalled = new AtomicBoolean(false);
        AtomicBoolean secondHandlerCalled = new AtomicBoolean(false);
        TestEvent testEvent = new TestEvent("Test");

        DomainEventHandler<TestEvent> firstHandler = event -> firstHandlerCalled.set(true);
        DomainEventHandler<TestEvent> secondHandler = event -> secondHandlerCalled.set(true);

        // Register both handlers
        publisher.register(TestEvent.class, firstHandler);
        publisher.register(TestEvent.class, secondHandler);

        // Act
        publisher.publish(testEvent);

        // Assert
        assertTrue(firstHandlerCalled.get(), "First handler should have been called");
        assertTrue(secondHandlerCalled.get(), "Second handler should have been called");
    }

    @Test
    void testAsyncPublishDispatchesToRegisteredHandler() throws InterruptedException {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        TestEvent testEvent = new TestEvent("Async Test");

        DomainEventHandler<TestEvent> handler = event -> {
            // Simulate some processing time
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            handlerCalled.set(true);
        };

        // Register the handler
        publisher.register(TestEvent.class, handler);

        // Act
        publisher.publishAsync(testEvent);

        // Wait a bit for the async processing to complete
        Thread.sleep(200);

        // Assert
        assertTrue(handlerCalled.get(), "Handler should have been called asynchronously");
    }

    @Test
    void testGetInstanceReturnsSingleton() {
        // Act
        DomainEventPublisher instance1 = DomainEventPublisher.getInstance();
        DomainEventPublisher instance2 = DomainEventPublisher.getInstance();

        // Assert
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    // Note: We can't test setInstance directly because the constructor is private
    // This method is primarily for testing purposes in a controlled environment
    // where a mock instance can be created through other means (like reflection)

    @Test
    void testShutdownClosesExecutorService() {
        // This is mostly a coverage test since we can't easily verify the executor is shut down
        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            publisher.shutdown();
            // Calling shutdown again should not throw
            publisher.shutdown();
        });
    }
}
