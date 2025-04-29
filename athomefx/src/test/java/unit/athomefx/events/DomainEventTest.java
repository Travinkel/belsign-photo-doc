package unit.athomefx.events;


import events.AbstractDomainEvent;
import events.DomainEventHandler;
import events.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventTest {
    
    private DomainEventPublisher publisher;
    
    @BeforeEach
    void setUp() {
        publisher = DomainEventPublisher.getInstance();
    }
    
    @AfterEach
    void tearDown() {
        // No need to clean up as the publisher is a singleton
    }
    
    @Test
    void testPublishEvent() {
        // Create a test event
        TestEvent event = new TestEvent("Test message");
        
        // Create a handler that sets a flag when the event is handled
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        DomainEventHandler<TestEvent> handler = e -> {
            assertEquals("Test message", e.getMessage());
            eventHandled.set(true);
        };
        
        // Register the handler
        publisher.register(TestEvent.class, handler);
        
        // Publish the event
        publisher.publish(event);
        
        // Verify that the event was handled
        assertTrue(eventHandled.get());
        
        // Unregister the handler
        publisher.unregister(TestEvent.class, handler);
    }
    
    @Test
    void testPublishAsyncEvent() throws InterruptedException {
        // Create a test event
        TestEvent event = new TestEvent("Test message");
        
        // Create a latch to wait for the event to be handled
        CountDownLatch latch = new CountDownLatch(1);
        
        // Create a handler that counts down the latch when the event is handled
        DomainEventHandler<TestEvent> handler = e -> {
            assertEquals("Test message", e.getMessage());
            latch.countDown();
        };
        
        // Register the handler
        publisher.register(TestEvent.class, handler);
        
        // Publish the event asynchronously
        publisher.publishAsync(event);
        
        // Wait for the event to be handled
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        
        // Unregister the handler
        publisher.unregister(TestEvent.class, handler);
    }
    
    @Test
    void testUnregisterHandler() {
        // Create a test event
        TestEvent event = new TestEvent("Test message");
        
        // Create a handler that throws an exception when the event is handled
        DomainEventHandler<TestEvent> handler = e -> {
            throw new RuntimeException("This handler should not be called");
        };
        
        // Register the handler
        publisher.register(TestEvent.class, handler);
        
        // Unregister the handler
        publisher.unregister(TestEvent.class, handler);
        
        // Publish the event
        publisher.publish(event);
        
        // If we get here without an exception, the test passes
    }
    
    @Test
    void testMultipleHandlers() {
        // Create a test event
        TestEvent event = new TestEvent("Test message");
        
        // Create counters for each handler
        AtomicBoolean handler1Called = new AtomicBoolean(false);
        AtomicBoolean handler2Called = new AtomicBoolean(false);
        
        // Create handlers that set flags when the event is handled
        DomainEventHandler<TestEvent> handler1 = e -> handler1Called.set(true);
        DomainEventHandler<TestEvent> handler2 = e -> handler2Called.set(true);
        
        // Register the handlers
        publisher.register(TestEvent.class, handler1);
        publisher.register(TestEvent.class, handler2);
        
        // Publish the event
        publisher.publish(event);
        
        // Verify that both handlers were called
        assertTrue(handler1Called.get());
        assertTrue(handler2Called.get());
        
        // Unregister the handlers
        publisher.unregister(TestEvent.class, handler1);
        publisher.unregister(TestEvent.class, handler2);
    }
    
    /**
     * Test event class for testing the domain event system.
     */
    private static class TestEvent extends AbstractDomainEvent {
        private final String message;
        
        public TestEvent(String message) {
            super();
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}