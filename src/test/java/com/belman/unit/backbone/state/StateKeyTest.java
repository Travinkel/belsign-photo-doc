package com.belman.unit.backbone.state;

import com.belman.backbone.core.state.StateKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StateKey class.
 */
public class StateKeyTest {

    @Test
    void of_shouldCreateStateKey() {
        // Act
        StateKey<String> key = StateKey.of("testKey", String.class);
        
        // Assert
        assertNotNull(key);
        assertEquals("testKey", key.getKey());
        assertEquals(String.class, key.getType());
    }
    
    @Test
    void of_withNullKey_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            StateKey.of(null, String.class);
        });
        
        assertTrue(exception.getMessage().contains("Key cannot be null"));
    }
    
    @Test
    void of_withNullType_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            StateKey.of("testKey", null);
        });
        
        assertTrue(exception.getMessage().contains("Type cannot be null"));
    }
    
    @Test
    void equals_withSameKey_shouldReturnTrue() {
        // Arrange
        StateKey<String> key1 = StateKey.of("testKey", String.class);
        StateKey<Integer> key2 = StateKey.of("testKey", Integer.class);
        
        // Act & Assert
        assertEquals(key1, key2);
    }
    
    @Test
    void equals_withDifferentKey_shouldReturnFalse() {
        // Arrange
        StateKey<String> key1 = StateKey.of("testKey1", String.class);
        StateKey<String> key2 = StateKey.of("testKey2", String.class);
        
        // Act & Assert
        assertNotEquals(key1, key2);
    }
    
    @Test
    void hashCode_withSameKey_shouldBeEqual() {
        // Arrange
        StateKey<String> key1 = StateKey.of("testKey", String.class);
        StateKey<Integer> key2 = StateKey.of("testKey", Integer.class);
        
        // Act & Assert
        assertEquals(key1.hashCode(), key2.hashCode());
    }
    
    @Test
    void toString_shouldIncludeKeyAndType() {
        // Arrange
        StateKey<String> key = StateKey.of("testKey", String.class);
        
        // Act
        String result = key.toString();
        
        // Assert
        assertTrue(result.contains("testKey"));
        assertTrue(result.contains("String"));
    }
}