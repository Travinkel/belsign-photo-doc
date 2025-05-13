package com.belman.unit.infrastructure.security;

import com.belman.common.config.SecureConfigStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SecureConfigStorage class.
 */
class SecureConfigStorageTest {

    private static final String TEST_KEY = "test.key";
    private static final String TEST_VALUE = "test.value";
    private static final String TEST_PROPERTIES_FILE = "test-config.properties";
    @TempDir
    Path tempDir;
    private SecureConfigStorage secureStorage;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test properties file
        createTestPropertiesFile();

        // Set the config directory to use a temporary directory for testing
        SecureConfigStorage.setConfigDirForTesting(tempDir.toFile().getAbsolutePath());

        // Get the instance (will be initialized with the temp directory)
        secureStorage = SecureConfigStorage.getInstance();

        // Clear any existing data
        secureStorage.clearAll();
    }

    /**
     * Creates a test properties file in the classpath.
     */
    private void createTestPropertiesFile() throws IOException {
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:test:localhost");
        props.setProperty("db.username", "testuser");
        props.setProperty("db.password", "testpassword");
        props.setProperty("db.driver", "org.test.Driver");

        File propsFile = new File(tempDir.toFile(), TEST_PROPERTIES_FILE);
        try (FileOutputStream fos = new FileOutputStream(propsFile)) {
            props.store(fos, "Test Properties");
        }

        // Add the temp directory to the classpath
        System.setProperty("java.class.path",
                System.getProperty("java.class.path") + File.pathSeparator + tempDir.toFile().getAbsolutePath());
    }

    @AfterEach
    void tearDown() throws Exception {
        // Reset the config directory to the default
        SecureConfigStorage.setConfigDirForTesting(System.getProperty("user.home") + File.separator + ".belsign");
    }

    @Test
    void storeAndRetrieveValue_shouldWorkCorrectly() {
        // Store a value
        boolean stored = secureStorage.storeValue(TEST_KEY, TEST_VALUE);

        // Verify it was stored successfully
        assertTrue(stored, "Value should be stored successfully");

        // Retrieve the value
        String retrievedValue = secureStorage.getValue(TEST_KEY);

        // Verify the retrieved value matches the original
        assertEquals(TEST_VALUE, retrievedValue, "Retrieved value should match the original");
    }

    @Test
    void getValueWithDefault_whenKeyExists_shouldReturnValue() {
        // Store a value
        secureStorage.storeValue(TEST_KEY, TEST_VALUE);

        // Retrieve with default
        String retrievedValue = secureStorage.getValue(TEST_KEY, "default");

        // Verify the retrieved value matches the original, not the default
        assertEquals(TEST_VALUE, retrievedValue, "Retrieved value should match the original, not the default");
    }

    @Test
    void getValueWithDefault_whenKeyDoesNotExist_shouldReturnDefault() {
        // Retrieve a non-existent key with default
        String defaultValue = "default";
        String retrievedValue = secureStorage.getValue("nonexistent.key", defaultValue);

        // Verify the retrieved value is the default
        assertEquals(defaultValue, retrievedValue, "Retrieved value should be the default");
    }

    @Test
    void removeValue_shouldRemoveTheValue() {
        // Store a value
        secureStorage.storeValue(TEST_KEY, TEST_VALUE);

        // Remove the value
        boolean removed = secureStorage.removeValue(TEST_KEY);

        // Verify it was removed successfully
        assertTrue(removed, "Value should be removed successfully");

        // Try to retrieve the removed value
        String retrievedValue = secureStorage.getValue(TEST_KEY);

        // Verify the value is null (removed)
        assertNull(retrievedValue, "Retrieved value should be null after removal");
    }

    @Test
    void clearAll_shouldRemoveAllValues() {
        // Store multiple values
        secureStorage.storeValue(TEST_KEY, TEST_VALUE);
        secureStorage.storeValue("another.key", "another.value");

        // Clear all values
        boolean cleared = secureStorage.clearAll();

        // Verify they were cleared successfully
        assertTrue(cleared, "Values should be cleared successfully");

        // Try to retrieve the cleared values
        String retrievedValue1 = secureStorage.getValue(TEST_KEY);
        String retrievedValue2 = secureStorage.getValue("another.key");

        // Verify the values are null (cleared)
        assertNull(retrievedValue1, "First value should be null after clearing");
        assertNull(retrievedValue2, "Second value should be null after clearing");
    }

    @Test
    void importFromProperties_shouldImportAllProperties() {
        // Create properties for testing
        Properties testProps = new Properties();
        testProps.setProperty("db.url", "jdbc:test:localhost");
        testProps.setProperty("db.username", "testuser");
        testProps.setProperty("db.password", "testpassword");
        testProps.setProperty("db.driver", "org.test.Driver");

        // Import using the testing method to avoid JavaFX dependency
        boolean imported = secureStorage.importFromPropertiesForTesting(testProps);

        // Verify the import was successful
        assertTrue(imported, "Properties should be imported successfully");

        // Verify the imported values
        assertEquals("jdbc:test:localhost", secureStorage.getValue("db.url"), "db.url should be imported correctly");
        assertEquals("testuser", secureStorage.getValue("db.username"), "db.username should be imported correctly");
        assertEquals("testpassword", secureStorage.getValue("db.password"), "db.password should be imported correctly");
        assertEquals("org.test.Driver", secureStorage.getValue("db.driver"), "db.driver should be imported correctly");
    }
}
