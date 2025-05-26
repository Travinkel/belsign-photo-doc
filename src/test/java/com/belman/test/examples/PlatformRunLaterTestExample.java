package com.belman.test.examples;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates how to test code that uses Platform.runLater().
 * It follows the recommended approach of:
 * 1. Extracting the logic from Platform.runLater() to separate methods
 * 2. Testing the extracted logic independently
 * 3. Using CountDownLatch for synchronization when testing with Platform.runLater()
 */
public class PlatformRunLaterTestExample {

    /**
     * A simple class that uses Platform.runLater() to update a property.
     * This represents a typical JavaFX component that needs to update UI elements
     * on the JavaFX application thread.
     */
    static class AsyncPropertyUpdater {
        private final StringProperty textProperty = new SimpleStringProperty("");

        /**
         * Updates the text property asynchronously using Platform.runLater().
         * This method is difficult to test directly because it uses Platform.runLater().
         */
        public void updateTextAsync(String newText) {
            Platform.runLater(() -> {
                updateTextSync(newText);
            });
        }

        /**
         * Updates the text property synchronously.
         * This method contains the logic extracted from Platform.runLater()
         * and can be tested independently.
         */
        public void updateTextSync(String newText) {
            if (newText == null) {
                throw new IllegalArgumentException("Text cannot be null");
            }
            textProperty.set(newText);
        }

        /**
         * Gets the text property.
         */
        public StringProperty textProperty() {
            return textProperty;
        }

        /**
         * Gets the current text value.
         */
        public String getText() {
            return textProperty.get();
        }
    }

    /**
     * Initialize the JavaFX toolkit before running tests.
     */
    @BeforeAll
    public static void initJavaFX() {
        // Initialize the JavaFX platform
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean initialized = new AtomicBoolean(false);

        try {
            // Try to initialize the JavaFX platform
            Platform.startup(() -> {
                initialized.set(true);
                latch.countDown();
            });

            // Wait for initialization to complete
            if (!latch.await(5, TimeUnit.SECONDS)) {
                System.err.println("JavaFX initialization timed out");
            }
        } catch (Exception e) {
            // Platform already initialized or other error
            System.out.println("[DEBUG_LOG] JavaFX platform initialization: " + e.getMessage());
            initialized.set(true);
            latch.countDown();
        }

        if (!initialized.get()) {
            throw new RuntimeException("Could not initialize JavaFX platform");
        }
    }

    /**
     * Test the extracted logic without using Platform.runLater().
     * This is the recommended approach for testing the core logic.
     */
    @Test
    public void testUpdateTextSync() {
        // Arrange
        AsyncPropertyUpdater updater = new AsyncPropertyUpdater();
        String expectedText = "Hello, World!";

        // Act
        updater.updateTextSync(expectedText);

        // Assert
        assertEquals(expectedText, updater.getText(), "Text should be updated synchronously");
    }

    /**
     * Test that the extracted logic correctly handles invalid input.
     */
    @Test
    public void testUpdateTextSync_WithNullText_ThrowsException() {
        // Arrange
        AsyncPropertyUpdater updater = new AsyncPropertyUpdater();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            updater.updateTextSync(null);
        }, "Should throw IllegalArgumentException when text is null");
    }

    /**
     * Test the asynchronous method that uses Platform.runLater().
     * This test uses CountDownLatch to wait for the asynchronous operation to complete.
     */
    @Test
    public void testUpdateTextAsync() throws Exception {
        // Arrange
        AsyncPropertyUpdater updater = new AsyncPropertyUpdater();
        String expectedText = "Hello, Async World!";
        CountDownLatch latch = new CountDownLatch(1);

        // Set up a listener to detect when the property changes
        updater.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(expectedText)) {
                latch.countDown();
            }
        });

        // Act
        updater.updateTextAsync(expectedText);

        // Wait for the async operation to complete
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed, "Async operation should complete within timeout");
        assertEquals(expectedText, updater.getText(), "Text should be updated asynchronously");
    }

    /**
     * Test a more complex scenario where multiple async updates are performed.
     * This demonstrates how to test a sequence of async operations.
     */
    @Test
    public void testMultipleAsyncUpdates() throws Exception {
        // Arrange
        AsyncPropertyUpdater updater = new AsyncPropertyUpdater();
        String[] updates = {"First", "Second", "Third", "Final"};
        CountDownLatch latch = new CountDownLatch(1);

        // Set up a listener to detect when the final update is applied
        updater.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(updates[updates.length - 1])) {
                latch.countDown();
            }
        });

        // Act - perform multiple async updates
        for (String update : updates) {
            updater.updateTextAsync(update);
        }

        // Wait for the final update to complete
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed, "All async operations should complete within timeout");
        assertEquals(updates[updates.length - 1], updater.getText(), 
                "Text should reflect the final update");
    }
}