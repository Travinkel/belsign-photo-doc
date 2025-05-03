package com.belman.unit.backbone.core.events;

import com.belman.domain.shared.AbstractDomainEvent;
import com.belman.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AbstractDomainEvent class.
 * These tests verify that the AbstractDomainEvent correctly implements the DomainEvent
 * interface and provides the expected behavior.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractDomainEventTest {

    // Test implementation of AbstractDomainEvent
    private static class TestEvent extends AbstractDomainEvent {
        private final String data;

        public TestEvent(String data) {
            super();
            this.data = data;
        }

        public TestEvent(UUID eventId, Instant timestamp, String eventType, String data) {
            super(eventId, timestamp, eventType);
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    @Test
    void constructor_withNoArgs_shouldGenerateIdAndTimestamp() {
        // Act
        TestEvent event = new TestEvent("test data");

        // Assert
        assertNotNull(event.getEventId(), "Event ID should not be null");
        assertNotNull(event.getTimestamp(), "Timestamp should not be null");
        assertEquals("TestEvent", event.getEventType(), "Event type should be the class name");
        assertEquals("test data", event.getData(), "Data should be stored correctly");
    }

    @Test
    void constructor_withArgs_shouldUseProvidedValues() {
        // Arrange
        UUID id = UUID.randomUUID();
        Instant timestamp = Instant.now();
        String eventType = "CustomEventType";

        // Act
        TestEvent event = new TestEvent(id, timestamp, eventType, "test data");

        // Assert
        assertEquals(id, event.getEventId(), "Event ID should match provided value");
        assertEquals(timestamp, event.getTimestamp(), "Timestamp should match provided value");
        assertEquals(eventType, event.getEventType(), "Event type should match provided value");
        assertEquals("test data", event.getData(), "Data should be stored correctly");
    }

    @Test
    void constructor_withNullId_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TestEvent(null, Instant.now(), "TestEvent", "test data");
        });

        assertTrue(exception.getMessage().contains("EventId, timestamp, and eventType cannot be null"),
                "Exception message should mention null eventId");
    }

    @Test
    void constructor_withNullTimestamp_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TestEvent(UUID.randomUUID(), null, "TestEvent", "test data");
        });

        assertTrue(exception.getMessage().contains("EventId, timestamp, and eventType cannot be null"),
                "Exception message should mention null timestamp");
    }

    @Test
    void constructor_withNullEventType_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TestEvent(UUID.randomUUID(), Instant.now(), null, "test data");
        });

        assertTrue(exception.getMessage().contains("EventId, timestamp, and eventType cannot be null"),
                "Exception message should mention null eventType");
    }

    @Test
    void toString_shouldIncludeEventDetails() {
        // Arrange
        UUID id = UUID.randomUUID();
        Instant timestamp = Instant.now();
        TestEvent event = new TestEvent(id, timestamp, "TestEvent", "test data");

        // Act
        String result = event.toString();

        // Assert
        assertTrue(result.contains("TestEvent"), "toString should include event type");
        assertTrue(result.contains(timestamp.toString()), "toString should include timestamp");
        assertTrue(result.contains(id.toString()), "toString should include event ID");
    }

    @Test
    void abstractDomainEvent_shouldImplementDomainEvent() {
        // Arrange
        TestEvent event = new TestEvent("test data");

        // Assert
        assertTrue(event instanceof DomainEvent, "AbstractDomainEvent should implement DomainEvent");
    }
}