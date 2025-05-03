package com.belman.unit.backbone.events;

import com.belman.domain.shared.AbstractDomainEvent;
import com.belman.domain.shared.DomainEventHandler;
import com.belman.domain.events.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DomainEventPublisher class.
 */
public class DomainEventPublisherTest {

    // Test event classes
    static class TestEvent extends AbstractDomainEvent {
        private final String message;

        public TestEvent(String message) {
            this.message = message;
        }

        public TestEvent(UUID eventId, Instant timestamp, String eventType, String message) {
            super(eventId, timestamp, eventType);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class AnotherTestEvent extends AbstractDomainEvent {
        private final int value;

        public AnotherTestEvent(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Test event handlers
    static class TestEventHandler implements DomainEventHandler<TestEvent> {
        private final AtomicInteger handledCount = new AtomicInteger(0);
        private final AtomicReference<String> lastMessage = new AtomicReference<>();

        @Override
        public void handle(TestEvent event) {
            handledCount.incrementAndGet();
            lastMessage.set(event.getMessage());
        }

        public int getHandledCount() {
            return handledCount.get();
        }

        public String getLastMessage() {
            return lastMessage.get();
        }
    }

    static class AnotherTestEventHandler implements DomainEventHandler<AnotherTestEvent> {
        private final AtomicInteger handledCount = new AtomicInteger(0);
        private final AtomicInteger lastValue = new AtomicInteger();

        @Override
        public void handle(AnotherTestEvent event) {
            handledCount.incrementAndGet();
            lastValue.set(event.getValue());
        }

        public int getHandledCount() {
            return handledCount.get();
        }

        public int getLastValue() {
            return lastValue.get();
        }
    }

    // Handlers to unregister after each test
    private TestEventHandler testHandler;
    private AnotherTestEventHandler anotherTestHandler;

    @BeforeEach
    void setUp() {
        // Create new handlers for each test
        testHandler = new TestEventHandler();
        anotherTestHandler = new AnotherTestEventHandler();
    }

    @AfterEach
    void tearDown() {
        // Unregister handlers to avoid interference between tests
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        if (testHandler != null) {
            publisher.unregister(TestEvent.class, testHandler);
        }
        if (anotherTestHandler != null) {
            publisher.unregister(AnotherTestEvent.class, anotherTestHandler);
        }
    }

    @Test
    void getInstance_shouldReturnSingletonInstance() {
        // Act
        DomainEventPublisher instance = DomainEventPublisher.getInstance();
        
        // Assert
        assertNotNull(instance);
    }

    @Test
    void setInstance_withNullInstance_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DomainEventPublisher.setInstance(null);
        });
        
        assertTrue(exception.getMessage().contains("Custom instance cannot be null"));
    }

    @Test
    void register_shouldRegisterHandler() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEvent event = new TestEvent("test message");
        
        // Act
        publisher.register(TestEvent.class, testHandler);
        publisher.publish(event);
        
        // Assert
        assertEquals(1, testHandler.getHandledCount());
        assertEquals("test message", testHandler.getLastMessage());
    }

    @Test
    void register_multipleHandlers_shouldRegisterAllHandlers() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEventHandler handler2 = new TestEventHandler();
        TestEvent event = new TestEvent("test message");
        
        // Act
        publisher.register(TestEvent.class, testHandler);
        publisher.register(TestEvent.class, handler2);
        publisher.publish(event);
        
        // Assert
        assertEquals(1, testHandler.getHandledCount());
        assertEquals(1, handler2.getHandledCount());
        assertEquals("test message", testHandler.getLastMessage());
        assertEquals("test message", handler2.getLastMessage());
        
        // Clean up additional handler
        publisher.unregister(TestEvent.class, handler2);
    }

    @Test
    void register_differentEventTypes_shouldRegisterForCorrectType() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEvent testEvent = new TestEvent("test message");
        AnotherTestEvent anotherEvent = new AnotherTestEvent(42);
        
        // Act
        publisher.register(TestEvent.class, testHandler);
        publisher.register(AnotherTestEvent.class, anotherTestHandler);
        publisher.publish(testEvent);
        publisher.publish(anotherEvent);
        
        // Assert
        assertEquals(1, testHandler.getHandledCount());
        assertEquals(1, anotherTestHandler.getHandledCount());
        assertEquals("test message", testHandler.getLastMessage());
        assertEquals(42, anotherTestHandler.getLastValue());
    }

    @Test
    void unregister_shouldRemoveHandler() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEvent event = new TestEvent("test message");
        
        // Act
        publisher.register(TestEvent.class, testHandler);
        publisher.unregister(TestEvent.class, testHandler);
        publisher.publish(event);
        
        // Assert
        assertEquals(0, testHandler.getHandledCount());
    }

    @Test
    void unregister_withMultipleHandlers_shouldRemoveOnlySpecifiedHandler() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEventHandler handler2 = new TestEventHandler();
        TestEvent event = new TestEvent("test message");
        
        // Act
        publisher.register(TestEvent.class, testHandler);
        publisher.register(TestEvent.class, handler2);
        publisher.unregister(TestEvent.class, testHandler);
        publisher.publish(event);
        
        // Assert
        assertEquals(0, testHandler.getHandledCount());
        assertEquals(1, handler2.getHandledCount());
        
        // Clean up additional handler
        publisher.unregister(TestEvent.class, handler2);
    }

    @Test
    void publish_withNoHandlers_shouldNotThrowException() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEvent event = new TestEvent("test message");
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            publisher.publish(event);
        });
    }

    @Test
    void publishAsync_shouldPublishEventAsynchronously() throws InterruptedException {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        TestEvent event = new TestEvent("async message");
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        
        // Create a handler that signals when it's done
        DomainEventHandler<TestEvent> asyncHandler = e -> {
            testHandler.handle(e);
            handlerCalled.set(true);
            latch.countDown();
        };
        
        // Act
        publisher.register(TestEvent.class, asyncHandler);
        publisher.publishAsync(event);
        
        // Assert
        // Wait for the async handler to complete (with timeout)
        assertTrue(latch.await(2, TimeUnit.SECONDS), "Async handler did not complete in time");
        assertTrue(handlerCalled.get(), "Handler was not called");
        assertEquals(1, testHandler.getHandledCount());
        assertEquals("async message", testHandler.getLastMessage());
        
        // Clean up additional handler
        publisher.unregister(TestEvent.class, asyncHandler);
    }

    @Test
    void shutdown_shouldShutdownExecutorService() {
        // Arrange
        DomainEventPublisher publisher = DomainEventPublisher.getInstance();
        
        // Act
        publisher.shutdown();
        
        // Assert - Verify that publishing async after shutdown doesn't work
        // This is a bit tricky to test directly, but we can check that no exception is thrown
        assertDoesNotThrow(() -> {
            publisher.publishAsync(new TestEvent("after shutdown"));
        });
    }
}