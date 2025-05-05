package com.belman.application.core;

import com.belman.domain.services.AuthenticationService;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.valueobjects.Username;
import com.belman.domain.aggregates.User;
import com.belman.infrastructure.service.SessionManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

/**
 * Utility class to register and inject services for the application.
 * Use this class to set up dependency injection for both production and testing.
 */
public final class ServiceInjector {

    private ServiceInjector() {
        // Private constructor to prevent instantiation
    }

    /**
     * Registers all services needed for the application.
     * This method should be called at application startup before any views are loaded.
     */
    public static void registerServices() {
        // Register a mock authentication service for development/testing
        MockAuthenticationService mockAuthService = new MockAuthenticationService();

        // Register the mock service
        ServiceLocator.registerService(AuthenticationService.class, mockAuthService);

        // Initialize SessionManager with the mock service
        SessionManager sessionManager = SessionManager.getInstance(mockAuthService);

        // Register other services as needed
        // ServiceLocator.registerService(EmailService.class, new SmtpEmailService(...));
    }

    /**
     * A simple mock implementation of AuthenticationService for testing purposes.
     * This implementation allows any username/password combination where they match.
     * In production, this would be replaced with a real implementation.
     */
    private static class MockAuthenticationService implements AuthenticationService {
        private User currentUser = null;

        @Override
        public Optional<User> authenticate(String username, String password) {
            System.out.println("Mock AuthenticationService: authenticate called with username: " + username);

            // For testing, accept any username/password where they match
            if (username != null && !username.trim().isEmpty() && username.equals(password)) {
                // Create a test user
                Username userUsername = new Username(username);
                HashedPassword hashedPassword = new HashedPassword(password); // In real system, this would be hashed
                EmailAddress email = new EmailAddress(username + "@example.com");
                User.Role role = User.Role.PRODUCTION; // Default role

                // Assign specific roles based on username for testing
                if (username.toLowerCase().contains("admin")) {
                    role = User.Role.ADMIN;
                } else if (username.toLowerCase().contains("qa")) {
                    role = User.Role.QA;
                }

                User user = new User(userUsername, hashedPassword, email);
                // Add the assigned role
                user.addRole(role);

                currentUser = user;

                System.out.println("Mock auth successful. User created with role: " + role);
                return Optional.of(user);
            }

            System.out.println("Mock auth failed for username: " + username);
            return Optional.empty();
        }

        @Override
        public Optional<User> getCurrentUser() {
            return Optional.ofNullable(currentUser);
        }

        @Override
        public boolean isLoggedIn() {
            return currentUser != null;
        }

        @Override
        public void logout() {
            currentUser = null;
            System.out.println("Mock AuthenticationService: User logged out");
        }
    }
}