package com.belman.unit.bootstrap.di;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.application.usecase.security.DefaultAuthenticationService;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserRepository;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the ServiceRegistry class.
 * These tests verify that the ServiceRegistry correctly registers services with the ServiceLocator.
 */
public class ServiceRegistryTest {

    @Mock
    private LoggerFactory mockLoggerFactory;

    @Mock
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock logger
        when(mockLoggerFactory.getLogger(any())).thenReturn(mockLogger);

        // Clear the ServiceLocator before each test
        ServiceLocator.clear();

        // Set the logger for ServiceRegistry
        ServiceRegistry.setLogger(mockLoggerFactory);
    }

    @Test
    public void testRegisterService_RegistersServiceUnderAllInterfaces() {
        // Arrange
        UserRepository userRepository = new InMemoryUserRepository(false);
        DefaultAuthenticationService authService = new DefaultAuthenticationService(userRepository);

        // Act
        ServiceRegistry.registerService(authService);

        // Assert
        // Should be registered under its class
        assertSame(authService, ServiceLocator.getService(DefaultAuthenticationService.class));

        // Should be registered under ExtendedAuthenticationService interface
        assertSame(authService, ServiceLocator.getService(ExtendedAuthenticationService.class));

        // Should be registered under AuthenticationService interface (parent interface)
        assertSame(authService, ServiceLocator.getService(AuthenticationService.class));
    }
}
