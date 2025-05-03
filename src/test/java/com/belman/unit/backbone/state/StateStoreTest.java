package com.belman.unit.backbone.state;

import com.belman.domain.shared.Property;
import com.belman.domain.shared.StateStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StateStore class.
 */
public class StateStoreTest {

    @AfterEach
    void tearDown() {
        // Clear the state store after each test to avoid interference
        StateStore.getInstance().clear();
    }

    @Test
    void getInstance_shouldReturnSingletonInstance() {
        // Act
        StateStore instance1 = StateStore.getInstance();
        StateStore instance2 = StateStore.getInstance();
        
        // Assert
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }
    
    @Test
    void set_shouldStoreValue() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act
        store.set("testKey", "testValue");
        
        // Assert
        assertEquals("testValue", store.get("testKey"));
    }
    
    @Test
    void set_withNullKey_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.set(null, "testValue");
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void set_withNullValue_shouldStoreNullValue() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act
        store.set("testKey", null);
        
        // Assert
        assertNull(store.get("testKey"));
    }
    
    @Test
    void get_withNonExistentKey_shouldReturnNull() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act
        String value = store.get("nonExistentKey");
        
        // Assert
        assertNull(value);
    }
    
    @Test
    void get_withNullKey_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.get(null);
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void getProperty_shouldReturnProperty() {
        // Arrange
        StateStore store = StateStore.getInstance();
        store.set("testKey", "testValue");
        
        // Act
        Property<String> property = store.getProperty("testKey");
        
        // Assert
        assertNotNull(property);
        assertEquals("testValue", property.get());
    }
    
    @Test
    void getProperty_withNonExistentKey_shouldCreateNewProperty() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act
        Property<String> property = store.getProperty("newKey");
        
        // Assert
        assertNotNull(property);
        assertNull(property.get());
    }
    
    @Test
    void getProperty_withNullKey_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.getProperty(null);
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void listen_shouldNotifyListenerOfInitialValue() {
        // Arrange
        StateStore store = StateStore.getInstance();
        store.set("testKey", "initialValue");
        AtomicReference<String> listenerValue = new AtomicReference<>();
        Object owner = new Object();
        
        // Act
        store.listen("testKey", owner, (String value) -> listenerValue.set(value));
        
        // Assert
        assertEquals("initialValue", listenerValue.get());
    }
    
    @Test
    void listen_shouldNotifyListenerOfChanges() {
        // Arrange
        StateStore store = StateStore.getInstance();
        AtomicReference<String> listenerValue = new AtomicReference<>();
        Object owner = new Object();
        
        store.listen("testKey", owner, (String value) -> listenerValue.set(value));
        
        // Act
        store.set("testKey", "newValue");
        
        // Assert
        assertEquals("newValue", listenerValue.get());
    }
    
    @Test
    void listen_withNullKey_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        Object owner = new Object();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.listen(null, owner, (String value) -> {});
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void listen_withNullOwner_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.listen("testKey", null, (String value) -> {});
        });
        
        assertTrue(exception.getMessage().contains("Owner cannot be null"));
    }
    
    @Test
    void listen_withNullListener_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        Object owner = new Object();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.listen("testKey", owner, null);
        });
        
        assertTrue(exception.getMessage().contains("Listener cannot be null"));
    }
    
    @Test
    void unlisten_shouldStopNotifyingListener() {
        // Arrange
        StateStore store = StateStore.getInstance();
        AtomicInteger listenerCallCount = new AtomicInteger(0);
        Object owner = new Object();
        
        store.listen("testKey", owner, (String value) -> listenerCallCount.incrementAndGet());
        
        // Reset counter after initial notification (if any)
        listenerCallCount.set(0);
        
        // Act
        store.unlisten("testKey", owner);
        store.set("testKey", "newValue");
        
        // Assert
        assertEquals(0, listenerCallCount.get());
    }
    
    @Test
    void unlisten_withNullKey_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        Object owner = new Object();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.unlisten(null, owner);
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void unlisten_withNullOwner_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.unlisten("testKey", null);
        });
        
        assertTrue(exception.getMessage().contains("Owner cannot be null"));
    }
    
    @Test
    void update_shouldUpdateExistingValue() {
        // Arrange
        StateStore store = StateStore.getInstance();
        store.set("testKey", "initialValue");
        
        // Act
        store.update("testKey", (String value) -> value + "-updated");
        
        // Assert
        assertEquals("initialValue-updated", store.get("testKey"));
    }
    
    @Test
    void update_withNonExistentKey_shouldCreateNewValue() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act
        store.update("newKey", (String value) -> "newValue");
        
        // Assert
        assertEquals("newValue", store.get("newKey"));
    }
    
    @Test
    void update_shouldNotifyListeners() {
        // Arrange
        StateStore store = StateStore.getInstance();
        store.set("testKey", "initialValue");
        AtomicReference<String> listenerValue = new AtomicReference<>();
        Object owner = new Object();
        
        store.listen("testKey", owner, (String value) -> listenerValue.set(value));
        
        // Reset after initial notification
        listenerValue.set(null);
        
        // Act
        store.update("testKey", (String value) -> "updatedValue");
        
        // Assert
        assertEquals("updatedValue", listenerValue.get());
    }
    
    @Test
    void update_withNullKey_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.update(null, (String value) -> "updatedValue");
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void update_withNullUpdater_shouldThrowException() {
        // Arrange
        StateStore store = StateStore.getInstance();
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            store.update("testKey", null);
        });
        
        assertTrue(exception.getMessage().contains("Updater function cannot be null"));
    }
    
    @Test
    void clear_shouldRemoveAllValues() {
        // Arrange
        StateStore store = StateStore.getInstance();
        store.set("key1", "value1");
        store.set("key2", "value2");
        
        // Act
        store.clear();
        
        // Assert
        assertNull(store.get("key1"));
        assertNull(store.get("key2"));
    }
    
    @Test
    void clear_shouldRemoveAllListeners() {
        // Arrange
        StateStore store = StateStore.getInstance();
        AtomicInteger listenerCallCount = new AtomicInteger(0);
        Object owner = new Object();
        
        store.listen("testKey", owner, (String value) -> listenerCallCount.incrementAndGet());
        
        // Reset counter after initial notification (if any)
        listenerCallCount.set(0);
        
        // Act
        store.clear();
        store.set("testKey", "newValue");
        
        // Assert
        assertEquals(0, listenerCallCount.get());
    }
    
    @Test
    void multipleListeners_shouldAllBeNotified() {
        // Arrange
        StateStore store = StateStore.getInstance();
        AtomicInteger listener1CallCount = new AtomicInteger(0);
        AtomicInteger listener2CallCount = new AtomicInteger(0);
        Object owner1 = new Object();
        Object owner2 = new Object();
        
        store.listen("testKey", owner1, (String value) -> listener1CallCount.incrementAndGet());
        store.listen("testKey", owner2, (String value) -> listener2CallCount.incrementAndGet());
        
        // Reset counters after initial notification (if any)
        listener1CallCount.set(0);
        listener2CallCount.set(0);
        
        // Act
        store.set("testKey", "newValue");
        
        // Assert
        assertEquals(1, listener1CallCount.get());
        assertEquals(1, listener2CallCount.get());
    }
}