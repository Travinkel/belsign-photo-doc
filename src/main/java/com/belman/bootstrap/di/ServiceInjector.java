package com.belman.bootstrap.di;


import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.application.usecase.security.DefaultAuthenticationService;

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
        UserRepository userRepository = com.belman.bootstrap.di.ServiceLocator.getService(UserRepository.class);

        // Register the DefaultAuthenticationService
        com.belman.bootstrap.di.ServiceLocator.registerService(AuthenticationService.class,
                new DefaultAuthenticationService(userRepository));

    }

    /**
     * A simple mock implementation of AuthenticationService for testing purposes.
     * This implementation allows any username/password combination where they match.
     * In production, this would be replaced with a real implementation.
     */
    private static class MockAuthenticationService implements AuthenticationService {
        private UserBusiness currentUser = null;

        @Override
        public Optional<UserBusiness> authenticate(String username, String password) {
            System.out.println("Mock AuthenticationService: authenticate called with username: " + username);

            // For testing, accept any username/password where they match
            return Optional.ofNullable(username)
                    .filter(u -> !u.trim().isEmpty())
                    .filter(u -> u.equals(password))
                    .map(u -> {
                        // Create a test user
                        Username userUsername = new Username(u);
                        HashedPassword hashedPassword = new HashedPassword(password); // In real system, this would be hashed
                        EmailAddress email = new EmailAddress(u + "@example.com");

                        // Determine role based on username
                        UserRole role = java.util.stream.Stream.of(
                                new java.util.AbstractMap.SimpleEntry<>(u.toLowerCase().contains("admin"), UserRole.ADMIN),
                                new java.util.AbstractMap.SimpleEntry<>(u.toLowerCase().contains("qa"), UserRole.QA))
                                .filter(java.util.Map.Entry::getKey)
                                .map(java.util.Map.Entry::getValue)
                                .findFirst()
                                .orElse(UserRole.PRODUCTION);

                        UserBusiness user = UserBusiness.createNewUser(userUsername, hashedPassword, email);
                        // Add the assigned role
                        user.addRole(role);

                        currentUser = user;

                        System.out.println("Mock auth successful. User created with role: " + role);
                        return user;
                    });
        }

        @Override
        public Optional<UserBusiness> getCurrentUser() {
            return Optional.ofNullable(currentUser);
        }

        @Override
        public void logout() {
            Optional.ofNullable(currentUser)
                    .ifPresent(user -> {
                        currentUser = null;
                        System.out.println("Mock AuthenticationService: User logged out");
                    });
        }

        @Override
        public boolean isLoggedIn() {
            return Optional.ofNullable(currentUser)
                    .map(user -> true)
                    .orElse(false);
        }
    }
}
