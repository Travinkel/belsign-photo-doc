package com.belman.application.admin;

import com.belman.backbone.core.base.BaseService;
import com.belman.domain.aggregates.User;
import com.belman.domain.aggregates.User.Role;
import com.belman.domain.rbac.AccessDeniedException;
import com.belman.domain.rbac.AccessPolicy;
import com.belman.domain.rbac.RoleBasedAccessController;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.services.AuthenticationService;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.valueobjects.PersonName;
import com.belman.domain.valueobjects.UserId;
import com.belman.domain.valueobjects.Username;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for admin management operations.
 * This class is Gluon-aware and uses the backbone framework.
 */
public class AdminService extends BaseService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    private final UserRepository userRepository;
    private final RoleBasedAccessController accessController;

    /**
     * Creates a new AdminService with the specified UserRepository.
     * 
     * @param userRepository the user repository
     * @param currentUser the current user (must have ADMIN role)
     */
    public AdminService(UserRepository userRepository, User currentUser) {
        this.userRepository = userRepository;

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

        this.accessController = new RoleBasedAccessController(authService, new AccessPolicy(Role.ADMIN));
    }

    /**
     * Creates a new user.
     * 
     * @param username the username
     * @param password the password
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email address
     * @param roles the roles to assign
     * @return the created user
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public User createUser(String username, String password, String firstName, String lastName, 
                          String email, Role... roles) throws AccessDeniedException {
        accessController.checkAccess();

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
            HashedPassword.fromPlainText(password),
            new PersonName(firstName, lastName),
            new EmailAddress(email)
        );

        // Assign roles
        for (Role role : roles) {
            user.addRole(role);
        }

        // Save user
        userRepository.save(user);
        LOGGER.info("User created: " + user.getId().id());

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
        accessController.checkAccess();

        boolean deleted = userRepository.delete(userId);
        if (deleted) {
            LOGGER.info("User deleted: " + userId.id());
        } else {
            LOGGER.warning("User not found for deletion: " + userId.id());
        }

        return deleted;
    }

    /**
     * Assigns a role to a user.
     * 
     * @param userId the ID of the user
     * @param role the role to assign
     * @return true if the role was assigned, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean assignRole(UserId userId, Role role) throws AccessDeniedException {
        accessController.checkAccess();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.addRole(role);
            userRepository.save(user);
            LOGGER.info("Role assigned to user: " + role + " -> " + userId.id());
            return true;
        } else {
            LOGGER.warning("User not found for role assignment: " + userId.id());
            return false;
        }
    }

    /**
     * Removes a role from a user.
     * 
     * @param userId the ID of the user
     * @param role the role to remove
     * @return true if the role was removed, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean removeRole(UserId userId, Role role) throws AccessDeniedException {
        accessController.checkAccess();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.removeRole(role);
            userRepository.save(user);
            LOGGER.info("Role removed from user: " + role + " -> " + userId.id());
            return true;
        } else {
            LOGGER.warning("User not found for role removal: " + userId.id());
            return false;
        }
    }

    /**
     * Resets a user's password.
     * 
     * @param userId the ID of the user
     * @param newPassword the new password
     * @return true if the password was reset, false if the user was not found
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public boolean resetPassword(UserId userId, String newPassword) throws AccessDeniedException {
        accessController.checkAccess();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Create a new user with the same properties but a new password
            User updatedUser = new User(
                user.getId(),
                user.getUsername(),
                HashedPassword.fromPlainText(newPassword),
                user.getName(),
                user.getEmail()
            );

            // Copy roles
            for (Role role : user.getRoles()) {
                updatedUser.addRole(role);
            }

            userRepository.save(updatedUser);
            LOGGER.info("Password reset for user: " + userId.id());
            return true;
        } else {
            LOGGER.warning("User not found for password reset: " + userId.id());
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
        accessController.checkAccess();
        return userRepository.findAll();
    }

    /**
     * Gets all users with the specified role.
     * 
     * @param role the role to search for
     * @return a list of users with the specified role
     * @throws AccessDeniedException if the current user does not have ADMIN role
     */
    public List<User> getUsersByRole(Role role) throws AccessDeniedException {
        accessController.checkAccess();
        return userRepository.findByRole(role);
    }
}
