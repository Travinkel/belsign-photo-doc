package com.belman.functional;

import com.belman.bootstrap.config.ApplicationInitializer;
import com.belman.bootstrap.config.StorageTypeConfig;
import com.belman.bootstrap.config.StorageTypeManager;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test for verifying basic application functionality.
 * This test ensures that the core components of the application can be initialized
 * and that basic functionality works as expected.
 */
public class StartUpSmokeTest {

    @BeforeEach
    void setup() {
        System.out.println("[DEBUG_LOG] Setting up smoke test");

        // Reset storage type configuration and manager before each test
        StorageTypeConfig.reset();
        StorageTypeManager.reset();

        // Force memory mode for testing
        StorageTypeConfig.forceMemoryMode();

        // Register the logger factory
        LoggerFactory loggerFactory = EmojiLoggerFactory.getInstance();
        ServiceRegistry.registerService(loggerFactory);

        System.out.println("[DEBUG_LOG] Forced memory mode and registered logger factory");
    }

    @AfterEach
    void tearDown() {
        System.out.println("[DEBUG_LOG] Tearing down smoke test");

        // Shutdown the application after each test
        ApplicationInitializer.shutdown();
    }

    @Test
    @DisplayName("Storage type configuration can be initialized")
    void testStorageTypeConfigInitialization() {
        System.out.println("[DEBUG_LOG] Testing storage type configuration initialization");

        // Initialize storage type configuration
        StorageTypeConfig.initialize();

        // Verify that memory mode is the default
        assertTrue(StorageTypeConfig.isMemoryMode(), 
                "Default storage type should be MEMORY");

        System.out.println("[DEBUG_LOG] Storage type configuration initialization successful");
    }

    @Test
    @DisplayName("Storage type manager can be initialized")
    void testStorageTypeManagerInitialization() {
        System.out.println("[DEBUG_LOG] Testing storage type manager initialization");

        // Initialize storage type manager
        StorageTypeManager.initialize();

        // Verify that the data source is null in memory mode
        assertNull(StorageTypeManager.getActiveDataSource(), 
                "Data source should be null in memory mode");

        System.out.println("[DEBUG_LOG] Storage type manager initialization successful");
    }

    @Test
    @DisplayName("Application can be initialized")
    void testApplicationInitialization() {
        System.out.println("[DEBUG_LOG] Testing application initialization");

        // Initialize the application
        ApplicationInitializer.initialize();

        // Verify that the authentication service is registered
        AuthenticationService authService = ServiceLocator.getService(AuthenticationService.class);
        assertNotNull(authService, 
                "Authentication service should be registered");

        System.out.println("[DEBUG_LOG] Application initialization successful");
    }

    @Test
    @DisplayName("InMemoryUserRepository contains default users")
    void testInMemoryUserRepository() {
        System.out.println("[DEBUG_LOG] Testing InMemoryUserRepository");

        // Create an in-memory user repository
        InMemoryUserRepository userRepository = new InMemoryUserRepository();

        // Verify that the admin user exists
        Optional<UserBusiness> adminUser = userRepository.findByUsername(new Username("admin"));
        assertTrue(adminUser.isPresent(), 
                "Admin user should exist in the repository");

        // Verify that the production user exists
        Optional<UserBusiness> productionUser = userRepository.findByUsername(new Username("production"));
        assertTrue(productionUser.isPresent(), 
                "Production user should exist in the repository");

        System.out.println("[DEBUG_LOG] InMemoryUserRepository test successful");
    }

    @Test
    @DisplayName("Application services can be retrieved from ServiceRegistry")
    void testServiceRegistry() {
        System.out.println("[DEBUG_LOG] Testing service registry");

        // Initialize the application
        ApplicationInitializer.initialize();

        // Verify that essential services are registered
        assertNotNull(ServiceLocator.getService(AuthenticationService.class), 
                "Authentication service should be registered");

        System.out.println("[DEBUG_LOG] Service registry test successful");
    }
}
