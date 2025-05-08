package com.belman.acceptance;

import com.belman.data.config.DatabaseConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

/**
 * Base class for all acceptance tests.
 * Sets up the test environment and provides common functionality.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseAcceptanceTest {

    @BeforeAll
    void setupTestEnvironment() {
        System.out.println("[DEBUG_LOG] Setting up test environment for acceptance tests");
        try {
            // Initialize database configuration
            DatabaseConfig.initialize();
            System.out.println("[DEBUG_LOG] Database configuration initialized");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupTest() {
        System.out.println("[DEBUG_LOG] Setting up test case");
    }

    /**
     * Helper method to log debug information during tests.
     * 
     * @param message The message to log
     */
    protected void logDebug(String message) {
        System.out.println("[DEBUG_LOG] " + message);
    }
}