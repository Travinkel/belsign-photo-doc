package com.belman.unit.backbone.state;

import com.belman.domain.shared.NestedProperty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the NestedProperty class.
 */
public class NestedPropertyTest {

    @Test
    void getNestedValue_withMap_shouldReturnValue() {
        // Arrange
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");
        address.put("zipCode", "10001");

        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");
        user.put("address", address);

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(user);

        // Act
        String city = property.getNestedValue("address.city");
        String zipCode = property.getNestedValue("address.zipCode");
        String name = property.getNestedValue("name");

        // Assert
        assertEquals("New York", city);
        assertEquals("10001", zipCode);
        assertEquals("John", name);
    }

    @Test
    void getNestedValue_withNonExistentPath_shouldReturnNull() {
        // Arrange
        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(user);

        // Act
        String city = property.getNestedValue("address.city");

        // Assert
        assertNull(city);
    }

    @Test
    void getNestedValue_withNullRoot_shouldReturnNull() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(null);

        // Act
        String name = property.getNestedValue("name");

        // Assert
        assertNull(name);
    }

    @Test
    void getNestedValue_withEmptyPath_shouldThrowException() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(new HashMap<>());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            property.getNestedValue("");
        });

        assertTrue(exception.getMessage().contains("Path cannot be null or empty"));
    }

    @Test
    void getNestedValue_withNullPath_shouldThrowException() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(new HashMap<>());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            property.getNestedValue(null);
        });

        assertTrue(exception.getMessage().contains("Path cannot be null or empty"));
    }

    @Test
    void setNestedValue_withMap_shouldUpdateValue() {
        // Arrange
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");

        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");
        user.put("address", address);

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(user);

        // Act
        property.setNestedValue("address.city", "Boston");
        property.setNestedValue("name", "Jane");

        // Assert
        assertEquals("Boston", property.getNestedValue("address.city"));
        assertEquals("Jane", property.getNestedValue("name"));
    }

    @Test
    void setNestedValue_withNonExistentPath_shouldCreatePath() {
        // Arrange
        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(user);

        // Act
        property.setNestedValue("address.city", "New York");

        // Assert
        assertEquals("New York", property.getNestedValue("address.city"));
    }

    @Test
    void setNestedValue_withNullRoot_shouldThrowException() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            property.setNestedValue("name", "John");
        });

        assertTrue(exception.getMessage().contains("Cannot set nested property on null object"));
    }

    @Test
    void setNestedValue_withEmptyPath_shouldThrowException() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(new HashMap<>());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            property.setNestedValue("", "value");
        });

        assertTrue(exception.getMessage().contains("Path cannot be null or empty"));
    }

    @Test
    void setNestedValue_withNullPath_shouldThrowException() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(new HashMap<>());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            property.setNestedValue(null, "value");
        });

        assertTrue(exception.getMessage().contains("Path cannot be null or empty"));
    }

    @Test
    void addNestedListener_shouldNotifyOnChange() {
        // Arrange
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");

        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");
        user.put("address", address);

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(user);
        AtomicReference<String> listenerValue = new AtomicReference<>();

        // Act
        property.addNestedListener("address.city", listenerValue::set);
        property.setNestedValue("address.city", "Boston");

        // Assert
        assertEquals("Boston", listenerValue.get());
    }

    @Test
    void addNestedListener_withNullListener_shouldThrowException() {
        // Arrange
        NestedProperty<Map<String, Object>> property = new NestedProperty<>(new HashMap<>());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            property.addNestedListener("path", null);
        });

        assertTrue(exception.getMessage().contains("Listener cannot be null"));
    }

    @Test
    void getNestedValue_withList_shouldReturnValue() {
        // Arrange
        List<String> names = new ArrayList<>();
        names.add("John");
        names.add("Jane");
        names.add("Bob");

        Map<String, Object> data = new HashMap<>();
        data.put("names", names);

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(data);

        // Act
        String name0 = property.getNestedValue("names.0");
        String name1 = property.getNestedValue("names.1");
        String name2 = property.getNestedValue("names.2");

        // Assert
        assertEquals("John", name0);
        assertEquals("Jane", name1);
        assertEquals("Bob", name2);
    }

    @Test
    void setNestedValue_withList_shouldUpdateValue() {
        // Arrange
        List<String> names = new ArrayList<>();
        names.add("John");
        names.add("Jane");

        Map<String, Object> data = new HashMap<>();
        data.put("names", names);

        NestedProperty<Map<String, Object>> property = new NestedProperty<>(data);

        // Act
        property.setNestedValue("names.0", "Bob");
        property.setNestedValue("names.2", "Alice");

        // Assert
        assertEquals("Bob", property.getNestedValue("names.0"));
        assertEquals("Jane", property.getNestedValue("names.1"));
        assertEquals("Alice", property.getNestedValue("names.2"));
    }

    // Test with a JavaBean
    static class Person {
        private String name;
        private Address address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    static class Address {
        private String city;
        private String zipCode;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }

    @Test
    void getNestedValue_withJavaBean_shouldReturnValue() {
        // Arrange
        Address address = new Address();
        address.setCity("New York");
        address.setZipCode("10001");

        Person person = new Person();
        person.setName("John");
        person.setAddress(address);

        NestedProperty<Person> property = new NestedProperty<>(person);

        // Act
        String city = property.getNestedValue("address.city");
        String zipCode = property.getNestedValue("address.zipCode");
        String name = property.getNestedValue("name");

        // Assert
        assertEquals("New York", city);
        assertEquals("10001", zipCode);
        assertEquals("John", name);
    }

    @Test
    void setNestedValue_withJavaBean_shouldUpdateValue() {
        // Arrange
        Address address = new Address();
        address.setCity("New York");

        Person person = new Person();
        person.setName("John");
        person.setAddress(address);

        NestedProperty<Person> property = new NestedProperty<>(person);

        // Act
        property.setNestedValue("address.city", "Boston");
        property.setNestedValue("name", "Jane");

        // Assert
        assertEquals("Boston", property.getNestedValue("address.city"));
        assertEquals("Jane", property.getNestedValue("name"));
    }
}