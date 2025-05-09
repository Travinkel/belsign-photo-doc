package com.belman.application.usecases.admin.service;

import com.belman.application.core.BaseService;
import com.belman.domain.aggregates.User;
import com.belman.domain.rbac.AccessDeniedException;
import com.belman.domain.rbac.AccessPolicy;
import com.belman.domain.rbac.RoleBasedAccessManager;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.services.AuthenticationService;
import com.belman.domain.services.PasswordHasher;
import com.belman.domain.valueobjects.*;

import java.util.List;
import java.util.Optional;

public class AdminService extends BaseService {
    private final UserRepository userRepository;
    private final RoleBasedAccessManager accessManager;
    private final PasswordHasher passwordHasher;

    /**
     * Creates a new AdminService with the specified UserRepository and PasswordHasher.
     *
     * @param userRepository the user repository
     * @param passwordHasher the password hasher
     * @param currentUser    the current user (must have ADMIN role)
     */
    public AdminService(UserRepository userRepository, PasswordHasher passwordHasher, User currentUser) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;

        // Create a simple AuthenticationService that always returns the current user
        AuthenticationService authService = new AuthenticationService() {
            @Override
            public Optional<User> authenticate(String username, String password) {
                return Optional.of(currentUser);
            }

            @Override
            public Optional<User> getCurrentUser() {
                return Optional.of(currentUser);
            }

            @Override
            public void logout() {
                // No-op
            }

            @Override
            public boolean isLoggedIn() {
                return true;
            }
        };

        this.accessManager = new RoleBasedAccessManager(authService, new AccessPolicy(User.Role.ADMIN));
    }

    /**
     * Creates a new user.
     *
     * @param username  the username
     * @param password  the password
     * @param firstName the first name
     * @param lastName  the last name
     * @param email     the email address
     * @param roles     the roles to assign
     * @return the created user
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public User createUser(String username, String password, String firstName, String lastName,
                           String email, User.Role... roles) throws AccessDeniedException {
        accessManager.checkAccess();

        // Check if username or email already exists
        Optional<User> existingUser = userRepository.findByUsername(new Username(username));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        existingUser = userRepository.findByEmail(new EmailAddress(email));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Create new user
        User user = new User(
                UserId.newId(),
                new Username(username),
                HashedPassword.fromPlainText(password, passwordHasher),
                new PersonName(firstName, lastName),
                new EmailAddress(email)
        );

        // Assign roles
        for (User.Role role : roles) {
            user.addRole(role);
        }

        // Save user
        userRepository.save(user);
        logInfo("User created: " + user.getId().id());

        return user;
    }

    /**
     * Deletes a user.
     *
     * @param userId the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean deleteUser(UserId userId) throws AccessDeniedException {
        accessManager.checkAccess();

        boolean deleted = userRepository.delete(userId);
        if (deleted) {
            logInfo("User deleted: " + userId.id());
        } else {
            logWarn("User not found for deletion: " + userId.id());
        }

        return deleted;
    }

    /**
     * Assigns a role to a user.
     *
     * @param userId the ID of the user
     * @param role   the role to assign
     * @return true if the role was assigned, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean assignRole(UserId userId, User.Role role) throws AccessDeniedException {
        accessManager.checkAccess();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.addRole(role);
            userRepository.save(user);
            logInfo("Role assigned to user: " + role + " -> " + userId.id());
            return true;
        } else {
            logWarn("User not found for role assignment: " + userId.id());
            return false;
        }
    }

    /**
     * Removes a role from a user.
     *
     * @param userId the ID of the user
     * @param role   the role to remove
     * @return true if the role was removed, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean removeRole(UserId userId, User.Role role) throws AccessDeniedException {
        accessManager.checkAccess();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.removeRole(role);
            userRepository.save(user);
            logInfo("Role removed from user: " + role + " -> " + userId.id());
            return true;
        } else {
            logWarn("User not found for role removal: " + userId.id());
            return false;
        }
    }

    /**
     * Resets a user's password.
     *
     * @param userId      the ID of the user
     * @param newPassword the new password
     * @return true if the password was reset, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean resetPassword(UserId userId, String newPassword) throws AccessDeniedException {
        accessManager.checkAccess();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Create a new user with the same properties but a new password
            User updatedUser = new User(
                    user.getId(),
                    user.getUsername(),
                    HashedPassword.fromPlainText(newPassword, passwordHasher),
                    user.getName(),
                    user.getEmail()
            );

            // Copy roles
            for (User.Role role : user.getRoles()) {
                updatedUser.addRole(role);
            }

            userRepository.save(updatedUser);
            logInfo("Password reset for user: " + userId.id());
            return true;
        } else {
            logWarn("User not found for password reset: " + userId.id());
            return false;
        }
    }

    /**
     * Gets all users.
     *
     * @return a list of all users
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public List<User> getAllUsers() throws AccessDeniedException {
        accessManager.checkAccess();
        return userRepository.findAll();
    }

    /**
     * Gets all users with the specified role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public List<User> getUsersByRole(User.Role role) throws AccessDeniedException {
        accessManager.checkAccess();
        return userRepository.findByRole(role);
    }
}
