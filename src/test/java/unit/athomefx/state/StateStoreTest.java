package unit.athomefx.state;

import com.belman.belsign.framework.athomefx.state.StateStore;
import javafx.beans.property.ObjectProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class StateStoreTest {

    private StateStore stateStore;

    @BeforeEach
    void setUp() {
        stateStore = StateStore.getInstance();
        stateStore.clear(); // Start with a clean state
    }

    @AfterEach
    void tearDown() {
        stateStore.clear(); // Clean up after each test
    }

    @Test
    void testSetAndGet() {
        // Test with string value
        stateStore.set("testString", "Hello World");
        assertEquals("Hello World", stateStore.get("testString"));

        // Test with integer value
        stateStore.set("testInt", 42);
        assertEquals(42, (int) stateStore.get("testInt"));

        // Test with object value
        TestObject obj = new TestObject("Test", 123);
        stateStore.set("testObject", obj);
        TestObject retrievedObj = stateStore.get("testObject");
        assertEquals(obj.name, retrievedObj.name);
        assertEquals(obj.value, retrievedObj.value);

        // Test overwriting a value
        stateStore.set("testString", "Updated Value");
        assertEquals("Updated Value", stateStore.get("testString"));

        // Test getting a non-existent value
        assertNull(stateStore.get("nonExistentKey"));
    }

    @Test
    void testGetProperty() {
        // Set a value and get its property
        stateStore.set("testProp", "Property Value");
        ObjectProperty<String> property = stateStore.getProperty("testProp");
        assertEquals("Property Value", property.get());

        // Update the property and verify the state is updated
        property.set("Updated Property");
        assertEquals("Updated Property", stateStore.get("testProp"));

        // Get a property for a non-existent key
        ObjectProperty<String> newProperty = stateStore.getProperty("newProp");
        assertNotNull(newProperty);
        assertNull(newProperty.get());

        // Set a value through the property
        newProperty.set("New Property Value");
        assertEquals("New Property Value", stateStore.get("newProp"));
    }

    @Test
    void testListeners() {
        // Set up a listener
        AtomicReference<String> listenerValue = new AtomicReference<>();
        stateStore.listen("testListener", this, (String value) -> {
            listenerValue.set(value);
        });

        // Set a value and verify the listener was called
        stateStore.set("testListener", "Listener Test");
        assertEquals("Listener Test", listenerValue.get());

        // Update the value and verify the listener was called again
        stateStore.set("testListener", "Updated Listener Test");
        assertEquals("Updated Listener Test", listenerValue.get());

        // Unregister the listener
        stateStore.unlisten("testListener", this);

        // Update the value again and verify the listener was not called
        stateStore.set("testListener", "Final Update");
        assertEquals("Updated Listener Test", listenerValue.get()); // Should not have changed
    }

    @Test
    void testUpdate() {
        // Set an initial value
        stateStore.set("testUpdate", 10);

        // Update the value using a function
        stateStore.update("testUpdate", (Integer value) -> value * 2);
        assertEquals(20, (int) stateStore.get("testUpdate"));

        // Update again
        stateStore.update("testUpdate", (Integer value) -> value + 5);
        assertEquals(25, (int) stateStore.get("testUpdate"));

        // Update a non-existent value (should handle null)
        stateStore.update("nonExistentUpdate", (String value) -> value == null ? "Default" : value + " Updated");
        assertEquals("Default", stateStore.get("nonExistentUpdate"));
    }

    @Test
    void testClear() {
        // Set some values
        stateStore.set("test1", "Value 1");
        stateStore.set("test2", "Value 2");

        // Verify they exist
        assertEquals("Value 1", stateStore.get("test1"));
        assertEquals("Value 2", stateStore.get("test2"));

        // Clear the store
        stateStore.clear();

        // Verify values are gone
        assertNull(stateStore.get("test1"));
        assertNull(stateStore.get("test2"));
    }

    @Test
    void testMultipleListeners() {
        AtomicInteger listener1Count = new AtomicInteger(0);
        AtomicInteger listener2Count = new AtomicInteger(0);

        // Register two listeners for the same key
        stateStore.listen("multiTest", "listener1", (String value) -> {
            listener1Count.incrementAndGet();
        });

        stateStore.listen("multiTest", "listener2", (String value) -> {
            listener2Count.incrementAndGet();
        });

        // Set a value and verify both listeners were called
        stateStore.set("multiTest", "Test Value");
        assertEquals(1, listener1Count.get());
        assertEquals(1, listener2Count.get());

        // Unregister one listener
        stateStore.unlisten("multiTest", "listener1");

        // Update the value and verify only the second listener was called
        stateStore.set("multiTest", "Updated Value");
        assertEquals(1, listener1Count.get()); // Should not have changed
        assertEquals(2, listener2Count.get());
    }

    // Simple test class for object storage tests
    private static class TestObject {
        public String name;
        public int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
