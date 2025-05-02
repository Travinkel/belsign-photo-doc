package com.belman.infrastructure.service;

import com.belman.backbone.core.base.BaseService;
import com.belman.backbone.core.events.DomainEvents;
import com.belman.domain.aggregates.User;
import com.belman.domain.events.UserLoggedInEvent;
import com.belman.domain.events.UserLoggedOutEvent;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.services.AuthenticationService;
import com.belman.domain.valueobjects.Username;

import java.util.Optional;

/**
 * Default implementation of the AuthenticationService interface.
 */
public class DefaultAuthenticationService extends BaseService implements AuthenticationService {
    private final UserRepository userRepository;
    private User currentUser;

    /**
     * Creates a new DefaultAuthenticationService with the specified UserRepository.
     * 
     * @param userRepository the user repository
     */
    public DefaultAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        try {
            // Find the user by username
            Optional<User> userOpt = userRepository.findByUsername(new Username(username));

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Check if the user is active
                if (!user.isActive()) {
                    logWarn("Authentication failed: User {} is not active", username);
                    return Optional.empty();
                }

                boolean isValid = false;

                // Check predefined users for simplicity
                // In a real application, we would use a more secure approach
                if (username.equals("admin") && password.equals("admin")) {
                    isValid = true;
                } else if (username.equals("production") && password.equals("production")) {
                    isValid = true;
                } else if (username.equals("qa") && password.equals("qa")) {
                    isValid = true;
                } else {
                    // Check if the password matches the user's password
                    isValid = user.getPassword().matches(password);
                }

                if (isValid) {
                    // Set the current user
                    currentUser = user;

                    // Publish a UserLoggedInEvent
                    publishEvent(new UserLoggedInEvent(user));

                    logInfo("User {} authenticated successfully", username);
                    return Optional.of(user);
                }
            }

            logWarn("Authentication failed for user: {}", username);
            return Optional.empty();
        } catch (Exception e) {
            logError("Error during authentication", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    @Override
    public void logout() {
        if (currentUser != null) {
            // Publish a UserLoggedOutEvent
            publishEvent(new UserLoggedOutEvent(currentUser));

            logInfo("User {} logged out", currentUser.getUsername().value());
            currentUser = null;
        }
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
