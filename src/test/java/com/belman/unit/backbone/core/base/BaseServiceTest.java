package com.belman.unit.backbone.core.base;

import com.belman.backbone.core.base.BaseService;
import com.belman.backbone.core.events.DomainEvent;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.backbone.core.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BaseService class.
 * These tests verify that the BaseService correctly handles event publishing
 * and logging operations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseServiceTest {

    // Test implementation of BaseService
    private static class TestService extends BaseService {
        public void publishTestEvent(DomainEvent event) {
            publishEvent(event);
        }

        public void publishTestEventAsync(DomainEvent event) {
            publishEventAsync(event);
        }

        public void logInfoTest(String message) {
            logInfo(message);
        }

        public void logInfoWithArgsTest(String message, Object... args) {
            logInfo(message, args);
        }

        public void logDebugTest(String message) {
            logDebug(message);
        }

        public void logDebugWithArgsTest(String message, Object... args) {
            logDebug(message, args);
        }

        public void logWarnTest(String message) {
            logWarn(message);
        }

        public void logWarnWithArgsTest(String message, Object... args) {
            logWarn(message, args);
        }

        public void logErrorTest(String message) {
            logError(message);
        }

        public void logErrorWithArgsTest(String message, Object... args) {
            logError(message, args);
        }

        public void logErrorWithThrowableTest(String message, Throwable throwable) {
            logError(message, throwable);
        }
    }

    // Test implementation of DomainEvent
    private static class TestEvent implements DomainEvent {
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

    @Mock
    private DomainEventPublisher mockPublisher;

    @Mock
    private Logger mockLogger;

    private TestService testService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create the test service
        testService = new TestService();

        // Replace the DomainEventPublisher instance with our mock
        Field instanceField = DomainEventPublisher.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockPublisher);

        // Replace the logger with our mock
        Field loggerField = BaseService.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(testService, mockLogger);
    }

    @Test
    void publishEvent_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent();

        // Act
        testService.publishTestEvent(testEvent);

        // Assert
        verify(mockPublisher).publish(testEvent);
    }

    @Test
    void publishEvent_withNullEvent_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.publishTestEvent(null);
        });

        assertEquals("Event cannot be null", exception.getMessage());
        verify(mockLogger).error("Cannot publish null event");
    }

    @Test
    void publishEventAsync_shouldDelegateToPublisher() {
        // Arrange
        TestEvent testEvent = new TestEvent();

        // Act
        testService.publishTestEventAsync(testEvent);

        // Assert
        verify(mockPublisher).publishAsync(testEvent);
    }

    @Test
    void publishEventAsync_withNullEvent_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.publishTestEventAsync(null);
        });

        assertEquals("Event cannot be null", exception.getMessage());
        verify(mockLogger).error("Cannot publish null event asynchronously");
    }

    @Test
    void logInfo_shouldDelegateToLogger() {
        // Act
        testService.logInfoTest("Test message");

        // Assert
        verify(mockLogger).info("Test message");
    }

    @Test
    void logInfoWithArgs_shouldDelegateToLogger() {
        // Act
        testService.logInfoWithArgsTest("Test message with {}", "arg");

        // Assert
        verify(mockLogger).info("Test message with {}", "arg");
    }

    @Test
    void logDebug_shouldDelegateToLogger() {
        // Act
        testService.logDebugTest("Test message");

        // Assert
        verify(mockLogger).debug("Test message");
    }

    @Test
    void logDebugWithArgs_shouldDelegateToLogger() {
        // Act
        testService.logDebugWithArgsTest("Test message with {}", "arg");

        // Assert
        verify(mockLogger).debug("Test message with {}", "arg");
    }

    @Test
    void logWarn_shouldDelegateToLogger() {
        // Act
        testService.logWarnTest("Test message");

        // Assert
        verify(mockLogger).warn("Test message");
    }

    @Test
    void logWarnWithArgs_shouldDelegateToLogger() {
        // Act
        testService.logWarnWithArgsTest("Test message with {}", "arg");

        // Assert
        verify(mockLogger).warn("Test message with {}", "arg");
    }

    @Test
    void logError_shouldDelegateToLogger() {
        // Act
        testService.logErrorTest("Test message");

        // Assert
        verify(mockLogger).error("Test message");
    }

    @Test
    void logErrorWithArgs_shouldDelegateToLogger() {
        // Act
        testService.logErrorWithArgsTest("Test message with {}", "arg");

        // Assert
        verify(mockLogger).error("Test message with {}", "arg");
    }

    @Test
    void logErrorWithThrowable_shouldDelegateToLogger() {
        // Arrange
        Exception testException = new Exception("Test exception");

        // Act
        testService.logErrorWithThrowableTest("Test message", testException);

        // Assert
        verify(mockLogger).error("Test message", testException);
    }
}