package com.belman.unit.backbone.core.events;

import com.belman.domain.events.AbstractDomainEvent;
import com.belman.domain.events.DomainEvent;
import com.belman.domain.events.DomainEventPublisher;
import com.belman.domain.events.DomainEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DomainEvents utility class.
 * These tests verify that the DomainEvents class correctly delegates to the DomainEventPublisher
 * and provides the expected fluent API for publishing events.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DomainEventsTest {

    // Test implementation of AbstractDomainEvent
    private static class TestEvent extends AbstractDomainEvent {
        private final String data;

        public TestEvent(String data) {
            super();
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    @Mock
    private DomainEventPublisher mockPublisher;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Replace the DomainEventPublisher instance with our mock
        Field instanceField = DomainEventPublisher.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockPublisher);
    }

    @Test
    void publish_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");

        // Act
        DomainEvents.publish(testEvent);

        // Assert
        verify(mockPublisher).publish(testEvent);
    }

    @Test
    void publish_withNullEvent_shouldNotCallPublisher() {
        // Act
        DomainEvents.publish(null);

        // Assert
        verify(mockPublisher, never()).publish(any());
    }

    @Test
    void publishAsync_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");

        // Act
        DomainEvents.publishAsync(testEvent);

        // Assert
        verify(mockPublisher).publishAsync(testEvent);
    }

    @Test
    void publishAsync_withNullEvent_shouldNotCallPublisher() {
        // Act
        DomainEvents.publishAsync(null);

        // Assert
        verify(mockPublisher, never()).publishAsync(any());
    }

    @Test
    void publishIf_withTrueCondition_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");

        // Act
        DomainEvents.publishIf(true, testEvent);

        // Assert
        verify(mockPublisher).publish(testEvent);
    }

    @Test
    void publishIf_withFalseCondition_shouldNotCallPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");

        // Act
        DomainEvents.publishIf(false, testEvent);

        // Assert
        verify(mockPublisher, never()).publish(any());
    }

    @Test
    void publishIf_withNullEvent_shouldNotCallPublisher() {
        // Act
        DomainEvents.publishIf(true, null);

        // Assert
        verify(mockPublisher, never()).publish(any());
    }

    @Test
    void publishAsyncIf_withTrueCondition_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");

        // Act
        DomainEvents.publishAsyncIf(true, testEvent);

        // Assert
        verify(mockPublisher).publishAsync(testEvent);
    }

    @Test
    void publishAsyncIf_withFalseCondition_shouldNotCallPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");

        // Act
        DomainEvents.publishAsyncIf(false, testEvent);

        // Assert
        verify(mockPublisher, never()).publishAsync(any());
    }

    @Test
    void publishAsyncIf_withNullEvent_shouldNotCallPublisher() {
        // Act
        DomainEvents.publishAsyncIf(true, null);

        // Assert
        verify(mockPublisher, never()).publishAsync(any());
    }

    @Test
    void publishIfPresent_withNonNullEvent_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");
        Supplier<DomainEvent> eventSupplier = () -> testEvent;

        // Act
        DomainEvents.publishIfPresent(eventSupplier);

        // Assert
        verify(mockPublisher).publish(testEvent);
    }

    @Test
    void publishIfPresent_withNullEvent_shouldNotCallPublisher() {
        // Arrange
        Supplier<DomainEvent> eventSupplier = () -> null;

        // Act
        DomainEvents.publishIfPresent(eventSupplier);

        // Assert
        verify(mockPublisher, never()).publish(any());
    }

    @Test
    void publishIfPresent_withNullSupplier_shouldNotCallPublisher() {
        // Act
        DomainEvents.publishIfPresent(null);

        // Assert
        verify(mockPublisher, never()).publish(any());
    }

    @Test
    void publishAsyncIfPresent_withNonNullEvent_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");
        Supplier<DomainEvent> eventSupplier = () -> testEvent;

        // Act
        DomainEvents.publishAsyncIfPresent(eventSupplier);

        // Assert
        verify(mockPublisher).publishAsync(testEvent);
    }

    @Test
    void publishAsyncIfPresent_withNullEvent_shouldNotCallPublisher() {
        // Arrange
        Supplier<DomainEvent> eventSupplier = () -> null;

        // Act
        DomainEvents.publishAsyncIfPresent(eventSupplier);

        // Assert
        verify(mockPublisher, never()).publishAsync(any());
    }

    @Test
    void publishAsyncIfPresent_withNullSupplier_shouldNotCallPublisher() {
        // Act
        DomainEvents.publishAsyncIfPresent(null);

        // Assert
        verify(mockPublisher, never()).publishAsync(any());
    }

    @Test
    void on_shouldRegisterHandlerWithPublisher() {
        // Arrange
        Consumer<TestEvent> handler = event -> {};

        // Act
        DomainEvents.on(TestEvent.class, handler);

        // Assert
        verify(mockPublisher).register(eq(TestEvent.class), any());
    }

    @Test
    void on_withNullEventType_shouldThrowException() {
        // Arrange
        Consumer<TestEvent> handler = event -> {};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DomainEvents.on(null, handler);
        });
    }

    @Test
    void on_withNullHandler_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DomainEvents.on(TestEvent.class, null);
        });
    }

    @Test
    void off_shouldUnregisterHandlerWithPublisher() {
        // Arrange
        Consumer<TestEvent> handler = event -> {};

        // Act
        DomainEvents.off(TestEvent.class, handler);

        // Assert
        verify(mockPublisher).unregister(eq(TestEvent.class), any());
    }

    @Test
    void off_withNullEventType_shouldThrowException() {
        // Arrange
        Consumer<TestEvent> handler = event -> {};

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DomainEvents.off(null, handler);
        });
    }

    @Test
    void off_withNullHandler_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DomainEvents.off(TestEvent.class, null);
        });
    }

    @Test
    void handlerRegistration_shouldWorkEndToEnd() {
        // Arrange
        TestEvent testEvent = new TestEvent("test data");
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        Consumer<TestEvent> handler = event -> handlerCalled.set(true);

        // Use the real publisher for this test
        try {
            // Reset the publisher to the real instance
            // We need to restore the original publisher instance
            Field instanceField = DomainEventPublisher.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, DomainEventPublisher.getInstance());

            // Act
            DomainEvents.on(TestEvent.class, handler);
            DomainEvents.publish(testEvent);

            // Assert
            assertTrue(handlerCalled.get(), "Handler should have been called");

            // Clean up
            DomainEvents.off(TestEvent.class, handler);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
