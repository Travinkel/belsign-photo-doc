package unit.athomefx.events;

import com.belman.belsign.framework.athomefx.events.AbstractDomainEvent;
import com.belman.belsign.framework.athomefx.events.DomainEvent;
import com.belman.belsign.framework.athomefx.events.DomainEvents;
import com.belman.belsign.framework.athomefx.events.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventsTest {

    @BeforeEach
    void setUp() {
        // No setup needed
    }

    @AfterEach
    void tearDown() {
        // No teardown needed
    }

    @Test
    void testPublish() {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        DomainEvents.on(TestEvent.class, event -> {
            eventHandled.set(true);
        });

        // Publish an event
        DomainEvents.publish(new TestEvent());

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Clean up
        DomainEvents.off(TestEvent.class, event -> {});
    }

    @Test
    void testPublishAsync() throws InterruptedException {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        DomainEvents.on(TestEvent.class, event -> {
            eventHandled.set(true);
            latch.countDown();
        });

        // Publish an event asynchronously
        DomainEvents.publishAsync(new TestEvent());

        // Wait for the event to be handled
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Clean up
        DomainEvents.off(TestEvent.class, event -> {});
    }

    @Test
    void testPublishIf() {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        DomainEvents.on(TestEvent.class, event -> {
            eventHandled.set(true);
        });

        // Publish an event if condition is true
        DomainEvents.publishIf(true, new TestEvent());

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Reset the flag
        eventHandled.set(false);

        // Publish an event if condition is false
        DomainEvents.publishIf(false, new TestEvent());

        // Verify that the event was not published
        assertFalse(eventHandled.get());

        // Clean up
        DomainEvents.off(TestEvent.class, event -> {});
    }

    @Test
    void testPublishAsyncIf() throws InterruptedException {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        DomainEvents.on(TestEvent.class, event -> {
            eventHandled.set(true);
            latch.countDown();
        });

        // Publish an event asynchronously if condition is true
        DomainEvents.publishAsyncIf(true, new TestEvent());

        // Wait for the event to be handled
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Reset the flag
        eventHandled.set(false);

        // Publish an event asynchronously if condition is false
        DomainEvents.publishAsyncIf(false, new TestEvent());

        // Wait a bit to make sure the event is not handled
        Thread.sleep(100);

        // Verify that the event was not published
        assertFalse(eventHandled.get());

        // Clean up
        DomainEvents.off(TestEvent.class, event -> {});
    }

    @Test
    void testPublishIfPresent() {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        DomainEvents.on(TestEvent.class, event -> {
            eventHandled.set(true);
        });

        // Publish an event if present
        Supplier<DomainEvent> presentSupplier = () -> new TestEvent();
        DomainEvents.publishIfPresent(presentSupplier);

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Reset the flag
        eventHandled.set(false);

        // Publish an event if present, but the supplier returns null
        Supplier<DomainEvent> nullSupplier = () -> null;
        DomainEvents.publishIfPresent(nullSupplier);

        // Verify that the event was not published
        assertFalse(eventHandled.get());

        // Clean up
        DomainEvents.off(TestEvent.class, event -> {});
    }

    @Test
    void testPublishAsyncIfPresent() throws InterruptedException {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        DomainEvents.on(TestEvent.class, event -> {
            eventHandled.set(true);
            latch.countDown();
        });

        // Publish an event asynchronously if present
        Supplier<DomainEvent> presentSupplier = () -> new TestEvent();
        DomainEvents.publishAsyncIfPresent(presentSupplier);

        // Wait for the event to be handled
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Reset the flag
        eventHandled.set(false);

        // Publish an event asynchronously if present, but the supplier returns null
        Supplier<DomainEvent> nullSupplier = () -> null;
        DomainEvents.publishAsyncIfPresent(nullSupplier);

        // Wait a bit to make sure the event is not handled
        Thread.sleep(100);

        // Verify that the event was not published
        assertFalse(eventHandled.get());

        // Clean up
        DomainEvents.off(TestEvent.class, event -> {});
    }

    @Test
    void testOnAndOff() {
        // Create a handler for the test event
        AtomicBoolean eventHandled = new AtomicBoolean(false);

        // Store the handler in a variable so we can use the same instance for both on() and off()
        java.util.function.Consumer<TestEvent> handler = event -> {
            eventHandled.set(true);
        };

        // Register the handler
        DomainEvents.on(TestEvent.class, handler);

        // Publish an event
        DomainEvents.publish(new TestEvent());

        // Verify that the event was published and handled
        assertTrue(eventHandled.get());

        // Reset the flag
        eventHandled.set(false);

        // Unregister the handler using the same instance
        DomainEvents.off(TestEvent.class, handler);

        // Publish another event
        DomainEvents.publish(new TestEvent());

        // Verify that the event was not handled
        assertFalse(eventHandled.get());
    }

    /**
     * Test event for testing event publishing.
     */
    static class TestEvent extends AbstractDomainEvent {
        // No additional fields or methods needed
    }
}
