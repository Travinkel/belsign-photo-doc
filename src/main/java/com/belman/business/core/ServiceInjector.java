package com.belman.business.core;


import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.security.AuthenticationService;
import com.belman.business.richbe.security.HashedPassword;
import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.user.UserRepository;
import com.belman.business.richbe.user.UserRole;
import com.belman.business.richbe.user.Username;
import com.belman.data.security.DefaultAuthenticationService;
import com.belman.data.service.SessionManager;

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
        // Get the UserRepository service
        UserRepository userRepository = ServiceLocator.getService(UserRepository.class);

        // Register the DefaultAuthenticationService
        ServiceLocator.registerService(AuthenticationService.class, new DefaultAuthenticationService(userRepository));

        // Initialize SessionManager with the registered AuthenticationService
        SessionManager sessionManager = SessionManager.getInstance(
            ServiceLocator.getService(AuthenticationService.class)
        );

        // Register other services as needed
        // ServiceLocator.registerService(EmailService.class, new SmtpEmailService(...));
    }

    /**
     * A simple mock implementation of AuthenticationService for testing purposes.
     * This implementation allows any username/password combination where they match.
     * In production, this would be replaced with a real implementation.
     */
    private static class MockAuthenticationService implements AuthenticationService {
        private UserAggregate currentUser = null;

        @Override
        public Optional<UserAggregate> authenticate(String username, String password) {
            System.out.println("Mock AuthenticationService: authenticate called with username: " + username);

            // For testing, accept any username/password where they match
            if (username != null && !username.trim().isEmpty() && username.equals(password)) {
                // Create a test user
                Username userUsername = new Username(username);
                HashedPassword hashedPassword = new HashedPassword(password); // In real system, this would be hashed
                EmailAddress email = new EmailAddress(username + "@example.com");
                UserRole role = UserRole.PRODUCTION; // Default role

                // Assign specific roles based on username for testing
                if (username.toLowerCase().contains("admin")) {
                    role = UserRole.ADMIN;
                } else if (username.toLowerCase().contains("qa")) {
                    role = UserRole.QA;
                }

                UserAggregate user = UserAggregate.createNewUser(userUsername, hashedPassword, email);
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
        public Optional<UserAggregate> getCurrentUser() {
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
