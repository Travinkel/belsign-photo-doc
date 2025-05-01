package com.belman.unit;

import com.belman.backbone.core.events.AbstractDomainEvent;
import com.belman.backbone.core.events.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BaseService class.
 */
public class BaseServiceTest {

    private TestService service;
    private DomainEventPublisher mockPublisher;
    private DomainEventPublisher originalInstance;

    @BeforeEach
    void setUp() {
        // Store the original instance
        originalInstance = DomainEventPublisher.getInstance();

        // Create a mock DomainEventPublisher
        mockPublisher = mock(DomainEventPublisher.class);

        // Set the mock instance
        DomainEventPublisher.setInstance(mockPublisher);

        // Create the service
        service = new TestService();
    }

    @AfterEach
    void tearDown() {
        // Restore the original instance
        DomainEventPublisher.setInstance(originalInstance);
    }

    @Test
    @DisplayName("Should publish event")
    void shouldPublishEvent() {
        // Arrange
        TestEvent event = new TestEvent("Test event data");
        
        // Act
        service.testPublishEvent(event);
        
        // Assert
        verify(mockPublisher, times(1)).publish(event);
    }

    @Test
    @DisplayName("Should publish event asynchronously")
    void shouldPublishEventAsynchronously() {
        // Arrange
        TestEvent event = new TestEvent("Test event data");
        
        // Act
        service.testPublishEventAsync(event);
        
        // Assert
        verify(mockPublisher, times(1)).publishAsync(event);
    }

    @Test
    @DisplayName("Should throw exception when publishing null event")
    void shouldThrowExceptionWhenPublishingNullEvent() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.testPublishEvent(null),
                "Should throw IllegalArgumentException when publishing null event");
    }

    @Test
    @DisplayName("Should throw exception when publishing null event asynchronously")
    void shouldThrowExceptionWhenPublishingNullEventAsynchronously() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.testPublishEventAsync(null),
                "Should throw IllegalArgumentException when publishing null event asynchronously");
    }

    /**
     * Test implementation of BaseService for testing purposes.
     */
    private static class TestService extends BaseService {
        public void testPublishEvent(AbstractDomainEvent event) {
            publishEvent(event);
        }
        
        public void testPublishEventAsync(AbstractDomainEvent event) {
            publishEventAsync(event);
        }
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
}
