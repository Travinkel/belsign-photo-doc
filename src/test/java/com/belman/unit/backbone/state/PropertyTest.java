package com.belman.unit.backbone.state;

import com.belman.backbone.core.state.Property;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Property class.
 */
public class PropertyTest {

    @Test
    void constructor_withNoArguments_shouldCreatePropertyWithNullValue() {
        // Act
        Property<String> property = new Property<>();
        
        // Assert
        assertNull(property.get());
    }
    
    @Test
    void constructor_withInitialValue_shouldCreatePropertyWithSpecifiedValue() {
        // Act
        Property<String> property = new Property<>("test");
        
        // Assert
        assertEquals("test", property.get());
    }
    
    @Test
    void set_shouldChangePropertyValue() {
        // Arrange
        Property<String> property = new Property<>("initial");
        
        // Act
        property.set("updated");
        
        // Assert
        assertEquals("updated", property.get());
    }
    
    @Test
    void set_withSameValue_shouldNotNotifyListeners() {
        // Arrange
        Property<String> property = new Property<>("test");
        AtomicInteger listenerCallCount = new AtomicInteger(0);
        
        property.addListener(value -> listenerCallCount.incrementAndGet());
        
        // Reset counter after initial notification
        listenerCallCount.set(0);
        
        // Act
        property.set("test"); // Same value
        
        // Assert
        assertEquals(0, listenerCallCount.get());
    }
    
    @Test
    void set_withDifferentValue_shouldNotifyListeners() {
        // Arrange
        Property<String> property = new Property<>("initial");
        AtomicReference<String> listenerValue = new AtomicReference<>();
        
        property.addListener(value -> listenerValue.set(value));
        
        // Act
        property.set("updated");
        
        // Assert
        assertEquals("updated", listenerValue.get());
    }
    
    @Test
    void addListener_shouldReceiveInitialValueIfNotNull() {
        // Arrange
        Property<String> property = new Property<>("initial");
        AtomicReference<String> listenerValue = new AtomicReference<>();
        
        // Act
        property.addListener(value -> listenerValue.set(value));
        
        // Assert
        assertEquals("initial", listenerValue.get());
    }
    
    @Test
    void addListener_withNullInitialValue_shouldNotCallListener() {
        // Arrange
        Property<String> property = new Property<>();
        AtomicInteger listenerCallCount = new AtomicInteger(0);
        
        // Act
        property.addListener(value -> listenerCallCount.incrementAndGet());
        
        // Assert
        assertEquals(0, listenerCallCount.get());
    }
    
    @Test
    void addListener_withNullListener_shouldNotThrowException() {
        // Arrange
        Property<String> property = new Property<>("test");
        
        // Act & Assert
        assertDoesNotThrow(() -> property.addListener(null));
    }
    
    @Test
    void removeListener_shouldStopNotifyingRemovedListener() {
        // Arrange
        Property<String> property = new Property<>("initial");
        AtomicInteger listener1CallCount = new AtomicInteger(0);
        AtomicInteger listener2CallCount = new AtomicInteger(0);
        
        // Add two listeners
        property.addListener(value -> listener1CallCount.incrementAndGet());
        property.addListener(value -> listener2CallCount.incrementAndGet());
        
        // Reset counters after initial notification
        listener1CallCount.set(0);
        listener2CallCount.set(0);
        
        // Remove the first listener
        property.removeListener(value -> listener1CallCount.incrementAndGet());
        
        // Act
        property.set("updated");
        
        // Assert - Only the second listener should be notified
        assertEquals(0, listener1CallCount.get());
        assertEquals(1, listener2CallCount.get());
    }
    
    @Test
    void clearListeners_shouldRemoveAllListeners() {
        // Arrange
        Property<String> property = new Property<>("initial");
        AtomicInteger listener1CallCount = new AtomicInteger(0);
        AtomicInteger listener2CallCount = new AtomicInteger(0);
        
        // Add two listeners
        property.addListener(value -> listener1CallCount.incrementAndGet());
        property.addListener(value -> listener2CallCount.incrementAndGet());
        
        // Reset counters after initial notification
        listener1CallCount.set(0);
        listener2CallCount.set(0);
        
        // Act
        property.clearListeners();
        property.set("updated");
        
        // Assert - No listeners should be notified
        assertEquals(0, listener1CallCount.get());
        assertEquals(0, listener2CallCount.get());
    }
    
    @Test
    void multipleListeners_shouldAllBeNotified() {
        // Arrange
        Property<String> property = new Property<>("initial");
        AtomicInteger listener1CallCount = new AtomicInteger(0);
        AtomicInteger listener2CallCount = new AtomicInteger(0);
        
        // Add two listeners
        property.addListener(value -> listener1CallCount.incrementAndGet());
        property.addListener(value -> listener2CallCount.incrementAndGet());
        
        // Reset counters after initial notification
        listener1CallCount.set(0);
        listener2CallCount.set(0);
        
        // Act
        property.set("updated");
        
        // Assert - Both listeners should be notified
        assertEquals(1, listener1CallCount.get());
        assertEquals(1, listener2CallCount.get());
    }
}