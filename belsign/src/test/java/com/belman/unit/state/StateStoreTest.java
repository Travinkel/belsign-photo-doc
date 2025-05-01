package dev.stefan.athomefx.core.state;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StateStore class.
 */
public class StateStoreTest {

    private StateStore stateStore;

    @BeforeEach
    void setUp() {
        stateStore = StateStore.getInstance();
        stateStore.clear(); // Ensure a clean state for each test
    }

    @Test
    @DisplayName("Should set and get a value correctly")
    void shouldSetAndGetValue() {
        // Arrange
        String key = "testKey";
        String value = "testValue";

        // Act
        stateStore.set(key, value);
        String retrievedValue = stateStore.get(key);

        // Assert
        assertEquals(value, retrievedValue, "The retrieved value should match the set value");
    }

    @Test
    @DisplayName("Should return null for non-existent key")
    void shouldReturnNullForNonExistentKey() {
        // Act
        String value = stateStore.get("nonExistentKey");

        // Assert
        assertNull(value, "Should return null for a non-existent key");
    }

    @Test
    @DisplayName("Should notify listeners when value changes")
    void shouldNotifyListenersWhenValueChanges() {
        // Arrange
        String key = "listenKey";
        AtomicReference<String> listenerValue = new AtomicReference<>();
        Object owner = new Object();

        // Act
        stateStore.listen(key, owner, (String value) -> listenerValue.set(value));
        stateStore.set(key, "initialValue");

        // Assert
        assertEquals("initialValue", listenerValue.get(), "Listener should be notified with the initial value");

        // Act again
        stateStore.set(key, "updatedValue");

        // Assert again
        assertEquals("updatedValue", listenerValue.get(), "Listener should be notified with the updated value");
    }

    @Test
    @DisplayName("Should stop notifying listeners after unlisten")
    void shouldStopNotifyingAfterUnlisten() {
        // Arrange
        String key = "unlistenKey";
        AtomicInteger callCount = new AtomicInteger(0);
        Object owner = new Object();

        // Act
        stateStore.listen(key, owner, (String value) -> callCount.incrementAndGet());
        stateStore.set(key, "value1");

        // Assert
        assertEquals(1, callCount.get(), "Listener should be called once");

        // Act again
        stateStore.unlisten(key, owner);
        stateStore.set(key, "value2");

        // Assert again
        assertEquals(1, callCount.get(), "Listener should not be called after unlisten");
    }

    @Test
    @DisplayName("Should update value using updater function")
    void shouldUpdateValueUsingUpdaterFunction() {
        // Arrange
        String key = "updateKey";
        stateStore.set(key, 5);

        // Act
        stateStore.update(key, (Integer value) -> value * 2);
        Integer updatedValue = stateStore.get(key);

        // Assert
        assertEquals(10, updatedValue, "Value should be updated using the updater function");
    }

    @Test
    @DisplayName("Should clear all state values")
    void shouldClearAllStateValues() {
        // Arrange
        stateStore.set("key1", "value1");
        stateStore.set("key2", "value2");

        // Act
        stateStore.clear();

        // Assert
        assertNull(stateStore.get("key1"), "Value for key1 should be null after clear");
        assertNull(stateStore.get("key2"), "Value for key2 should be null after clear");
    }
}
