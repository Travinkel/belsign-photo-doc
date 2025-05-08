package com.belman.business.usecases.admin.service;

import com.belman.business.core.BaseService;
import com.belman.business.domain.common.EmailAddress;
import com.belman.business.domain.common.PersonName;
import com.belman.business.domain.user.*;
import com.belman.business.domain.exceptions.AccessDeniedException;
import com.belman.business.domain.user.rbac.AccessPolicy;
import com.belman.business.domain.user.rbac.RoleBasedAccessManager;
import com.belman.business.domain.security.AuthenticationService;
import com.belman.business.domain.security.HashedPassword;
import com.belman.business.domain.security.PasswordHasher;
import com.belman.business.domain.services.LoggerFactory;
import com.belman.business.domain.user.factory.UserAggregateFactory;
import com.belman.data.logging.EmojiLoggerFactory;

import java.util.List;
import java.util.Optional;

public class AdminService extends BaseService {
    private final UserRepository userRepository;
    private final RoleBasedAccessManager accessManager;
    private final PasswordHasher passwordHasher;
    private final AuthenticationService authenticationService;

    /**
     * Creates a new AdminService with the specified UserRepository, PasswordHasher and AuthenticationService.
     *
     * @param userRepository    the user repository
     * @param passwordHasher    the password hasher
     * @param authenticationService the authentication service
     */
    public AdminService(UserRepository userRepository, PasswordHasher passwordHasher,
                        AuthenticationService authenticationService) {
        super(EmojiLoggerFactory.getInstance());
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.authenticationService = authenticationService;

        this.accessManager = new RoleBasedAccessManager(authenticationService, new AccessPolicy(UserRole.ADMIN));
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
    public UserAggregate createUser(String username, String password, String firstName, String lastName,
                                    String email, UserRole... roles) throws AccessDeniedException {
        accessManager.checkAccess();

        // Check if username or email already exists
        Optional<UserAggregate> existingUser = userRepository.findByUsername(new Username(username));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        existingUser = userRepository.findByEmail(new EmailAddress(email));
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Create new user
        UserAggregate user = UserAggregateFactory.createUserWithName(
                new Username(username),
                HashedPassword.fromPlainText(password, passwordHasher),
                new PersonName(firstName, lastName),
                new EmailAddress(email)
        );

        // Assign roles
        for (UserRole role : roles) {
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
    public boolean assignRole(UserId userId, UserRole role) throws AccessDeniedException {
        accessManager.checkAccess();

        Optional<UserAggregate> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserAggregate user = userOpt.get();
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
    public boolean removeRole(UserId userId, UserRole role) throws AccessDeniedException {
        accessManager.checkAccess();

        Optional<UserAggregate> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserAggregate user = userOpt.get();
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

        Optional<UserAggregate> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserAggregate user = userOpt.get();
            // Create a new user with the same properties but a new password
            UserAggregate updatedUser = UserAggregateFactory.createFullUser(
                    user.getId(),
                    user.getUsername(),
                    HashedPassword.fromPlainText(newPassword, passwordHasher),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber()
            );

            // Copy roles
            for (UserRole role : user.roles()) {
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
    public List<UserAggregate> getAllUsers() throws AccessDeniedException {
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
    public List<UserAggregate> getUsersByRole(UserRole role) throws AccessDeniedException {
        accessManager.checkAccess();
        return userRepository.findByRole(role);
    }
}
