package dev.stefan.athomefx.core.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Property class.
 */
public class PropertyTest {

    @Test
    @DisplayName("Should create property with null initial value")
    void shouldCreatePropertyWithNullInitialValue() {
        // Act
        Property<String> property = new Property<>();

        // Assert
        assertNull(property.get(), "Initial value should be null");
    }

    @Test
    @DisplayName("Should create property with specified initial value")
    void shouldCreatePropertyWithSpecifiedInitialValue() {
        // Arrange
        String initialValue = "initialValue";

        // Act
        Property<String> property = new Property<>(initialValue);

        // Assert
        assertEquals(initialValue, property.get(), "Initial value should match the specified value");
    }

    @Test
    @DisplayName("Should set and get value correctly")
    void shouldSetAndGetValueCorrectly() {
        // Arrange
        Property<Integer> property = new Property<>();
        Integer value = 42;

        // Act
        property.set(value);
        Integer retrievedValue = property.get();

        // Assert
        assertEquals(value, retrievedValue, "The retrieved value should match the set value");
    }

    @Test
    @DisplayName("Should notify listeners when value changes")
    void shouldNotifyListenersWhenValueChanges() {
        // Arrange
        Property<String> property = new Property<>();
        AtomicReference<String> listenerValue = new AtomicReference<>();

        // Act
        property.addListener(listenerValue::set);
        property.set("newValue");

        // Assert
        assertEquals("newValue", listenerValue.get(), "Listener should be notified with the new value");
    }

    @Test
    @DisplayName("Should not notify listeners when value doesn't change")
    void shouldNotNotifyListenersWhenValueDoesntChange() {
        // Arrange
        Property<String> property = new Property<>("value");
        AtomicInteger callCount = new AtomicInteger(0);

        // Act
        property.addListener(value -> callCount.incrementAndGet());
        // Reset counter after initial notification
        callCount.set(0);

        // Set the same value
        property.set("value");

        // Assert
        assertEquals(0, callCount.get(), "Listener should not be called when value doesn't change");
    }

    @Test
    @DisplayName("Should notify listener with current value when added")
    void shouldNotifyListenerWithCurrentValueWhenAdded() {
        // Arrange
        String initialValue = "initialValue";
        Property<String> property = new Property<>(initialValue);
        AtomicReference<String> listenerValue = new AtomicReference<>();

        // Act
        property.addListener(listenerValue::set);

        // Assert
        assertEquals(initialValue, listenerValue.get(), "Listener should be notified with the current value when added");
    }

    @Test
    @DisplayName("Should not notify listener after removal")
    void shouldNotNotifyListenerAfterRemoval() {
        // Arrange
        Property<String> property = new Property<>();
        AtomicInteger callCount = new AtomicInteger(0);

        // Create a listener that can be referenced later for removal
        Consumer<String> listener = value -> callCount.incrementAndGet();

        // Act
        property.addListener(listener);
        property.set("value1");

        // Assert
        assertEquals(1, callCount.get(), "Listener should be called once");

        // Act again
        property.removeListener(listener);
        property.set("value2");

        // Assert again
        assertEquals(1, callCount.get(), "Listener should not be called after removal");
    }

    @Test
    @DisplayName("Should clear all listeners")
    void shouldClearAllListeners() {
        // Arrange
        Property<String> property = new Property<>();
        AtomicInteger callCount1 = new AtomicInteger(0);
        AtomicInteger callCount2 = new AtomicInteger(0);

        // Act
        property.addListener(value -> callCount1.incrementAndGet());
        property.addListener(value -> callCount2.incrementAndGet());

        // Reset counters after initial notification
        callCount1.set(0);
        callCount2.set(0);

        // Clear all listeners
        property.clearListeners();
        property.set("newValue");

        // Assert
        assertEquals(0, callCount1.get(), "First listener should not be called after clearing");
        assertEquals(0, callCount2.get(), "Second listener should not be called after clearing");
    }
}
