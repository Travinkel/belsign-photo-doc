package com.belman.integration.backbone.core.events;

import com.belman.backbone.core.events.AbstractDomainEvent;
import com.belman.backbone.core.events.DomainEvent;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.backbone.core.events.DomainEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the domain events system.
 * These tests verify that the domain events system works correctly when multiple components
 * interact with each other.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DomainEventsIntegrationTest {

    // Test events
    private static class TestEvent extends AbstractDomainEvent {
        private final String message;

        public TestEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class AnotherTestEvent extends AbstractDomainEvent {
        private final int value;

        public AnotherTestEvent(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Test service that publishes events
    private static class TestEventPublisher {
        public void publishEvent(String message) {
            DomainEvents.publish(new TestEvent(message));
        }

        public void publishEventAsync(String message) {
            DomainEvents.publishAsync(new TestEvent(message));
        }

        public void publishAnotherEvent(int value) {
            DomainEvents.publish(new AnotherTestEvent(value));
        }
    }

    // Test service that handles events
    private static class TestEventHandler {
        private final List<String> receivedMessages = new ArrayList<>();
        private final List<Integer> receivedValues = new ArrayList<>();
        private final AtomicInteger eventCount = new AtomicInteger(0);

        public void handleTestEvent(TestEvent event) {
            receivedMessages.add(event.getMessage());
            eventCount.incrementAndGet();
        }

        public void handleAnotherTestEvent(AnotherTestEvent event) {
            receivedValues.add(event.getValue());
            eventCount.incrementAndGet();
        }

        public List<String> getReceivedMessages() {
            return receivedMessages;
        }

        public List<Integer> getReceivedValues() {
            return receivedValues;
        }

        public int getEventCount() {
            return eventCount.get();
        }
    }

    private TestEventPublisher publisher;
    private TestEventHandler handler;

    @BeforeEach
    void setUp() {
        publisher = new TestEventPublisher();
        handler = new TestEventHandler();

        // Register event handlers
        DomainEvents.on(TestEvent.class, handler::handleTestEvent);
        DomainEvents.on(AnotherTestEvent.class, handler::handleAnotherTestEvent);
    }

    @AfterEach
    void tearDown() {
        // Unregister event handlers to avoid affecting other tests
        DomainEvents.off(TestEvent.class, handler::handleTestEvent);
        DomainEvents.off(AnotherTestEvent.class, handler::handleAnotherTestEvent);
    }

    @Test
    void publishEvent_shouldBeReceivedByHandler() {
        // Act
        publisher.publishEvent("Hello, World!");

        // Assert
        assertEquals(1, handler.getEventCount());
        assertEquals(1, handler.getReceivedMessages().size());
        assertEquals("Hello, World!", handler.getReceivedMessages().get(0));
    }

    @Test
    void publishMultipleEvents_shouldAllBeReceivedByHandler() {
        // Act
        publisher.publishEvent("First message");
        publisher.publishEvent("Second message");
        publisher.publishEvent("Third message");

        // Assert
        assertEquals(3, handler.getEventCount());
        assertEquals(3, handler.getReceivedMessages().size());
        assertEquals("First message", handler.getReceivedMessages().get(0));
        assertEquals("Second message", handler.getReceivedMessages().get(1));
        assertEquals("Third message", handler.getReceivedMessages().get(2));
    }

    @Test
    void publishDifferentEventTypes_shouldBeRoutedToCorrectHandlers() {
        // Act
        publisher.publishEvent("Test message");
        publisher.publishAnotherEvent(42);

        // Assert
        assertEquals(2, handler.getEventCount());
        assertEquals(1, handler.getReceivedMessages().size());
        assertEquals("Test message", handler.getReceivedMessages().get(0));
        assertEquals(1, handler.getReceivedValues().size());
        assertEquals(42, handler.getReceivedValues().get(0));
    }

    @Test
    void publishAsyncEvent_shouldBeReceivedByHandler() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean eventReceived = new AtomicBoolean(false);

        // Register a handler that counts down the latch when the event is received
        DomainEvents.on(TestEvent.class, event -> {
            eventReceived.set(true);
            latch.countDown();
        });

        // Act
        publisher.publishEventAsync("Async message");

        // Wait for the event to be processed (with timeout)
        boolean completed = latch.await(1, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed, "Event should have been processed within the timeout");
        assertTrue(eventReceived.get(), "Event should have been received");
    }

    @Test
    void unregisterHandler_shouldStopReceivingEvents() {
        // Arrange - publish an event to verify the handler is working
        publisher.publishEvent("Initial message");
        assertEquals(1, handler.getEventCount());

        // Act - unregister the handler and publish another event
        DomainEvents.off(TestEvent.class, handler::handleTestEvent);
        publisher.publishEvent("This should not be received");

        // Assert - the event count should still be 1
        assertEquals(1, handler.getEventCount());
        assertEquals(1, handler.getReceivedMessages().size());
        assertEquals("Initial message", handler.getReceivedMessages().get(0));
    }
}