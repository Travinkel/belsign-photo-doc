package com.belman.unit.backbone.state;

import com.belman.backbone.core.api.CoreAPI;
import com.belman.backbone.core.state.Property;
import com.belman.backbone.core.state.StateKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the type-safe state management functionality in the CoreAPI class.
 */
public class TypeSafeStateStoreTest {

    // Define some type-safe keys for testing
    private static final StateKey<String> STRING_KEY = StateKey.of("stringKey", String.class);
    private static final StateKey<Integer> INTEGER_KEY = StateKey.of("integerKey", Integer.class);
    private static final StateKey<User> USER_KEY = StateKey.of("userKey", User.class);

    @AfterEach
    void tearDown() {
        // Clear the state store after each test to avoid interference
        CoreAPI.clearState();
    }

    @Test
    void setState_shouldStoreValue() {
        // Act
        CoreAPI.setState(STRING_KEY, "testValue");

        // Assert
        assertEquals("testValue", CoreAPI.getState(STRING_KEY));
    }

    @Test
    void setState_withNullKey_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CoreAPI.setState((StateKey<String>) null, "testValue");
        });

        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }

    @Test
    void getState_withCorrectType_shouldReturnValue() {
        // Arrange
        CoreAPI.setState(INTEGER_KEY, 42);

        // Act
        Integer value = CoreAPI.getState(INTEGER_KEY);

        // Assert
        assertEquals(42, value);
    }

    @Test
    void getStateProperty_shouldReturnProperty() {
        // Arrange
        CoreAPI.setState(STRING_KEY, "testValue");

        // Act
        Property<String> property = CoreAPI.getStateProperty(STRING_KEY);

        // Assert
        assertNotNull(property);
        assertEquals("testValue", property.get());
    }

    @Test
    void listenToState_shouldNotifyListener() {
        // Arrange
        AtomicReference<String> listenerValue = new AtomicReference<>();
        Object owner = new Object();

        CoreAPI.listenToState(STRING_KEY, owner, listenerValue::set);

        // Act
        CoreAPI.setState(STRING_KEY, "newValue");

        // Assert
        assertEquals("newValue", listenerValue.get());
    }

    @Test
    void updateState_shouldUpdateExistingValue() {
        // Arrange
        CoreAPI.setState(STRING_KEY, "initialValue");

        // Act
        CoreAPI.updateState(STRING_KEY, value -> value + "-updated");

        // Assert
        assertEquals("initialValue-updated", CoreAPI.getState(STRING_KEY));
    }

    @Test
    void unlistenToState_shouldStopNotifyingListener() {
        // Arrange
        AtomicInteger listenerCallCount = new AtomicInteger(0);
        Object owner = new Object();

        CoreAPI.listenToState(STRING_KEY, owner, value -> listenerCallCount.incrementAndGet());

        // Reset counter after initial notification (if any)
        listenerCallCount.set(0);

        // Act
        CoreAPI.unlistenToState(STRING_KEY, owner);
        CoreAPI.setState(STRING_KEY, "newValue");

        // Assert
        assertEquals(0, listenerCallCount.get());
    }

    // A simple class for testing complex objects in the state store
    static class User {
        private final String name;
        private final int age;

        User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }
    }

    @Test
    void complexObject_shouldBeStoredAndRetrieved() {
        // Arrange
        User user = new User("John", 30);

        // Act
        CoreAPI.setState(USER_KEY, user);
        User retrievedUser = CoreAPI.getState(USER_KEY);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals("John", retrievedUser.getName());
        assertEquals(30, retrievedUser.getAge());
    }
}
