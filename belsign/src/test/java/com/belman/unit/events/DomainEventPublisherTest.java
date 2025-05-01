package dev.stefan.athomefx.core.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DomainEventPublisher class.
 */
public class DomainEventPublisherTest {

    private DomainEventPublisher publisher;
    private TestEvent testEvent;
    private TestEventHandler testEventHandler;

    @BeforeEach
    void setUp() {
        publisher = DomainEventPublisher.getInstance();
        testEvent = new TestEvent("Test event data");
        testEventHandler = new TestEventHandler();
    }

    @AfterEach
    void tearDown() {
        // Unregister the handler to clean up
        publisher.unregister(TestEvent.class, testEventHandler);
    }

    @Test
    @DisplayName("Should register and publish event to handler")
    void shouldRegisterAndPublishEventToHandler() {
        // Arrange
        publisher.register(TestEvent.class, testEventHandler);

        // Act
        publisher.publish(testEvent);

        // Assert
        assertEquals(1, testEventHandler.getHandleCount(), "Handler should be called once");
        assertEquals(testEvent, testEventHandler.getLastEvent(), "Handler should receive the correct event");
    }

    @Test
    @DisplayName("Should not publish event to unregistered handler")
    void shouldNotPublishEventToUnregisteredHandler() {
        // Arrange
        publisher.register(TestEvent.class, testEventHandler);
        publisher.unregister(TestEvent.class, testEventHandler);

        // Act
        publisher.publish(testEvent);

        // Assert
        assertEquals(0, testEventHandler.getHandleCount(), "Handler should not be called after unregistering");
    }

    @Test
    @DisplayName("Should publish event asynchronously")
    void shouldPublishEventAsynchronously() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        DomainEventHandler<TestEvent> asyncHandler = event -> {
            handlerCalled.set(true);
            latch.countDown();
        };

        publisher.register(TestEvent.class, asyncHandler);

        // Act
        publisher.publishAsync(testEvent);

        // Assert
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Async handler should be called within timeout");
        assertTrue(handlerCalled.get(), "Handler should be called");

        // Clean up
        publisher.unregister(TestEvent.class, asyncHandler);
    }

    @Test
    @DisplayName("Should not publish event to handler of different event type")
    void shouldNotPublishEventToHandlerOfDifferentEventType() {
        // Arrange
        AnotherTestEvent anotherEvent = new AnotherTestEvent();
        publisher.register(TestEvent.class, testEventHandler);

        // Act
        publisher.publish(anotherEvent);

        // Assert
        assertEquals(0, testEventHandler.getHandleCount(), "Handler should not be called for different event type");
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

    /**
     * Another test implementation of AbstractDomainEvent for testing purposes.
     */
    private static class AnotherTestEvent extends AbstractDomainEvent {
        // Empty implementation for testing
    }

    /**
     * Test implementation of DomainEventHandlerImplementation for testing purposes.
     */
    private static class TestEventHandler implements DomainEventHandler<TestEvent> {
        private final AtomicInteger handleCount = new AtomicInteger(0);
        private TestEvent lastEvent;

        @Override
        public void handle(TestEvent event) {
            handleCount.incrementAndGet();
            lastEvent = event;
        }

        public int getHandleCount() {
            return handleCount.get();
        }

        public TestEvent getLastEvent() {
            return lastEvent;
        }
    }
}