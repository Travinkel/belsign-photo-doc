package com.belman.application.usecase.admin;

import com.belman.application.base.BaseService;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the AdminService interface.
 * This service provides administrative functionality for managing users.
 */
public class DefaultAdminService extends BaseService implements AdminService {
    // Log message constants
    private static final String LOG_USER_CREATED = "User created: {} with roles: {}";
    private static final String LOG_USER_CREATION_FAILED = "User creation failed: {}";
    private static final String LOG_USER_DELETED = "User deleted: {}";
    private static final String LOG_USER_DELETION_FAILED = "User deletion failed: {}";
    private static final String LOG_ROLE_ASSIGNED = "Role assigned: {} to user: {}";
    private static final String LOG_ROLE_ASSIGNMENT_FAILED = "Role assignment failed: {} to user: {}";
    private static final String LOG_ROLE_REMOVED = "Role removed: {} from user: {}";
    private static final String LOG_ROLE_REMOVAL_FAILED = "Role removal failed: {} from user: {}";
    private static final String LOG_PASSWORD_RESET = "Password reset for user: {}";
    private static final String LOG_PASSWORD_RESET_FAILED = "Password reset failed for user: {}";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new DefaultAdminService with the specified UserRepository and PasswordHasher.
     *
     * @param userRepository the user repository
     * @param passwordHasher the password hasher
     */
    public DefaultAdminService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this(userRepository, passwordHasher, ServiceLocator.getService(LoggerFactory.class));
    }

    /**
     * Creates a new DefaultAdminService with the specified UserRepository, PasswordHasher, and LoggerFactory.
     * This constructor is primarily used for testing.
     *
     * @param userRepository the user repository
     * @param passwordHasher the password hasher
     * @param loggerFactory the logger factory
     */
    public DefaultAdminService(UserRepository userRepository, PasswordHasher passwordHasher, LoggerFactory loggerFactory) {
        super(loggerFactory);
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.loggerFactory = loggerFactory;
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public List<UserBusiness> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public UserBusiness createUser(String username, String password, String firstName, String lastName, String email,
                                   UserRole[] roles) {
        try {
            // Validate username
            if (username == null || username.trim().isEmpty()) {
                logWarn(LOG_USER_CREATION_FAILED, "Username cannot be empty");
                throw new IllegalArgumentException("Username cannot be empty");
            }

            // Check if username is unique
            Username usernameObj = new Username(username);
            if (userRepository.findByUsername(usernameObj).isPresent()) {
                logWarn(LOG_USER_CREATION_FAILED, "Username already exists: " + username);
                throw new IllegalArgumentException("Username already exists");
            }

            // Validate password
            if (password == null || password.trim().isEmpty()) {
                logWarn(LOG_USER_CREATION_FAILED, "Password cannot be empty for user: " + username);
                throw new IllegalArgumentException("Password cannot be empty");
            }

            if (password.length() < 4) {
                logWarn(LOG_USER_CREATION_FAILED, "Password must be at least 4 characters long for user: " + username);
                throw new IllegalArgumentException("Password must be at least 4 characters long");
            }

            // Validate roles
            if (roles == null || roles.length == 0) {
                logWarn(LOG_USER_CREATION_FAILED, "User must have at least one role: " + username);
                throw new IllegalArgumentException("User must have at least one role");
            }

            boolean hasValidRole = false;
            for (UserRole role : roles) {
                if (role == UserRole.PRODUCTION || role == UserRole.QA || role == UserRole.ADMIN) {
                    hasValidRole = true;
                    break;
                }
            }

            if (!hasValidRole) {
                logWarn(LOG_USER_CREATION_FAILED, "User must have at least one valid role: " + username);
                throw new IllegalArgumentException("User must have at least one valid role (PRODUCTION, QA, ADMIN)");
            }

            // Create the user objects
            HashedPassword passwordObj = HashedPassword.fromPlainText(password, passwordHasher);
            PersonName nameObj = new PersonName(firstName, lastName);
            EmailAddress emailObj = new EmailAddress(email);

            // Create the user
            UserBusiness.Builder builder = new UserBusiness.Builder()
                    .id(UserId.newId())
                    .username(usernameObj)
                    .password(passwordObj)
                    .name(nameObj)
                    .email(emailObj);

            // Add roles
            for (UserRole role : roles) {
                builder.addRole(role);
            }

            UserBusiness user = builder.build();

            // Save the user
            userRepository.save(user);

            // Log successful user creation
            StringBuilder rolesStr = new StringBuilder();
            for (UserRole role : roles) {
                rolesStr.append(role.name()).append(", ");
            }
            logInfo(LOG_USER_CREATED, username, rolesStr.toString());

            return user;
        } catch (IllegalArgumentException e) {
            // Already logged in validation checks
            throw e; // Re-throw validation exceptions
        } catch (Exception e) {
            logError(LOG_USER_CREATION_FAILED, e, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteUser(UserId userId) {
        try {
            boolean result = userRepository.deleteById(userId);
            if (result) {
                logInfo(LOG_USER_DELETED, userId.id());
            } else {
                logWarn(LOG_USER_DELETION_FAILED, "User not found: " + userId.id());
            }
            return result;
        } catch (Exception e) {
            logError(LOG_USER_DELETION_FAILED, e, "Error deleting user: " + userId.id());
            return false;
        }
    }

    @Override
    public boolean assignRole(UserId userId, UserRole role) {
        try {
            // Find the user
            Optional<UserBusiness> userOpt = userRepository.findById(userId);

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();

                // Add the role
                user.addRole(role);

                // Save the user
                userRepository.save(user);

                // Log role assignment
                logInfo(LOG_ROLE_ASSIGNED, role.name(), userId.id());

                return true;
            } else {
                logWarn(LOG_ROLE_ASSIGNMENT_FAILED, role.name(), userId.id());
                return false;
            }
        } catch (Exception e) {
            logError(LOG_ROLE_ASSIGNMENT_FAILED, e, role.name(), userId.id());
            return false;
        }
    }

    @Override
    public boolean removeRole(UserId userId, UserRole role) {
        try {
            // Find the user
            Optional<UserBusiness> userOpt = userRepository.findById(userId);

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();

                // Remove the role
                user.removeRole(role);

                // Save the user
                userRepository.save(user);

                // Log role removal
                logInfo(LOG_ROLE_REMOVED, role.name(), userId.id());

                return true;
            } else {
                logWarn(LOG_ROLE_REMOVAL_FAILED, role.name(), userId.id());
                return false;
            }
        } catch (Exception e) {
            logError(LOG_ROLE_REMOVAL_FAILED, e, role.name(), userId.id());
            return false;
        }
    }

    @Override
    public boolean resetPassword(UserId userId, String newPassword) {
        try {
            // Find the user
            Optional<UserBusiness> userOpt = userRepository.findById(userId);

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();

                // Create a new hashed password
                HashedPassword passwordObj = HashedPassword.fromPlainText(newPassword, passwordHasher);

                // Create a new user with the updated password
                UserBusiness.Builder builder = new UserBusiness.Builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(passwordObj)
                        .name(user.getName())
                        .email(user.getEmail())
                        .approvalState(user.getApprovalState());

                // Add roles
                for (UserRole role : user.getRoles()) {
                    builder.addRole(role);
                }

                UserBusiness updatedUser = builder.build();

                // Save the user
                userRepository.save(updatedUser);

                // Log password reset
                logInfo(LOG_PASSWORD_RESET, userId.id());

                return true;
            } else {
                logWarn(LOG_PASSWORD_RESET_FAILED, "User not found: " + userId.id());
                return false;
            }
        } catch (Exception e) {
            logError(LOG_PASSWORD_RESET_FAILED, e, "Error resetting password for user: " + userId.id());
            return false;
        }
    }
}
