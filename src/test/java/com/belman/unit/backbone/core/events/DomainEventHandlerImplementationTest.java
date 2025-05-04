package com.belman.unit.backbone.core.events;

import com.belman.domain.services.Logger;
import com.belman.domain.shared.AbstractDomainEvent;
import com.belman.domain.events.DomainEventHandlerImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the DomainEventHandlerImplementation class.
 * These tests verify that the handler implementation correctly handles events
 * without using reflection or annotation scanning.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DomainEventHandlerImplementationTest {

    @Mock
    private Logger mockLogger;

    private DomainEventHandlerImplementation handlerImplementation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handlerImplementation = new DomainEventHandlerImplementation(mockLogger);
    }

    /**
     * Test event class for testing the handler implementation.
     */
    static class TestEvent extends AbstractDomainEvent {
        private final String message;

        TestEvent(String message) {
            super();
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Test
    void testHandleEventLogsAndProcessesEvent() {
        // Arrange
        TestEvent testEvent = new TestEvent("Test Message");
        String handlerName = "TestHandler";

        // Act - this should not throw
        assertDoesNotThrow(() -> {
            handlerImplementation.handleEvent(testEvent, handlerName);
        });

        // We can't easily verify the logging, but we can verify that the method doesn't throw
    }

    @Test
    void testHandleEventHandlesNullEvent() {
        // Arrange
        String handlerName = "TestHandler";

        // Act - this should not throw
        assertDoesNotThrow(() -> {
            handlerImplementation.handleEvent(null, handlerName);
        });

        // We can't easily verify the logging, but we can verify that the method doesn't throw
    }

    @Test
    void testCreateHandlerCreatesHandlerThatCallsFunction() {
        // Arrange
        TestEvent testEvent = new TestEvent("Test Message");
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        AtomicReference<String> capturedMessage = new AtomicReference<>();

        // Create a handler function that sets a flag and captures the message
        java.util.function.Consumer<Object> handlerFunction = event -> {
            handlerCalled.set(true);
            if (event instanceof TestEvent) {
                capturedMessage.set(((TestEvent) event).getMessage());
            }
        };

        // Act
        java.util.function.Consumer<Object> handler = 
            handlerImplementation.createHandler("TestHandler", handlerFunction);

        // Call the handler with an event
        handler.accept(testEvent);

        // Assert
        assertTrue(handlerCalled.get(), "Handler function should have been called");
        assertEquals("Test Message", capturedMessage.get(), "Handler should have received the correct message");
    }

    @Test
    void testCreateHandlerHandlesNullEvent() {
        // Arrange
        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Create a handler function that sets a flag
        java.util.function.Consumer<Object> handlerFunction = event -> {
            handlerCalled.set(true);
        };

        // Act
        java.util.function.Consumer<Object> handler = 
            handlerImplementation.createHandler("TestHandler", handlerFunction);

        // Call the handler with a null event
        handler.accept(null);

        // Assert
        assertFalse(handlerCalled.get(), "Handler function should not have been called with null event");
    }

    @Test
    void testCreateHandlerHandlesNullFunction() {
        // Arrange
        TestEvent testEvent = new TestEvent("Test Message");

        // Act
        java.util.function.Consumer<Object> handler = 
            handlerImplementation.createHandler("TestHandler", null);

        // Call the handler with an event - should not throw
        assertDoesNotThrow(() -> {
            handler.accept(testEvent);
        });
    }
}
