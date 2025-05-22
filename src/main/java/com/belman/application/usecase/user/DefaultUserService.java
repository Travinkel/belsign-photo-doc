package com.belman.application.usecase.user;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the UserService interface.
 * This service provides user management functionality.
 */
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Logger logger;

    /**
     * Creates a new DefaultUserService with the specified UserRepository, PasswordHasher, and LoggerFactory.
     *
     * @param userRepository the user repository
     * @param passwordHasher the password hasher
     * @param loggerFactory the logger factory
     */
    public DefaultUserService(UserRepository userRepository, PasswordHasher passwordHasher, LoggerFactory loggerFactory) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.logger = loggerFactory.getLogger(DefaultUserService.class);
        logger.info("DefaultUserService initialized");
    }

    @Override
    public Optional<UserBusiness> getUserById(UserId userId) {
        logger.debug("Getting user by ID: {}", userId.id());
        Optional<UserBusiness> user = userRepository.findById(userId);
        if (user.isPresent()) {
            logger.debug("Found user with ID: {}", userId.id());
        } else {
            logger.debug("User not found with ID: {}", userId.id());
        }
        return user;
    }

    @Override
    public Optional<UserBusiness> getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        try {
            com.belman.domain.user.Username usernameObj = new com.belman.domain.user.Username(username);
            Optional<UserBusiness> user = userRepository.findByUsername(usernameObj);
            if (user.isPresent()) {
                logger.debug("Found user with username: {}", username);
            } else {
                logger.debug("User not found with username: {}", username);
            }
            return user;
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid username format: {}", username, e);
            return Optional.empty();
        }
    }

    @Override
    public List<UserBusiness> getAllUsers() {
        logger.debug("Getting all users");
        try {
            List<UserBusiness> users = userRepository.findAll();
            logger.debug("Retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving all users", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<UserBusiness> getUsersByRole(UserRole role) {
        logger.debug("Getting users by role: {}", role);
        try {
            List<UserBusiness> users = userRepository.findByRole(role);
            logger.debug("Retrieved {} users with role {}", users.size(), role);
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving users with role: {}", role, e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateUserName(UserId userId, PersonName name) {
        logger.debug("Updating name for user with ID: {}", userId.id());
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            user.setName(name);
            userRepository.save(user);
            logger.info("Updated name for user: {}, ID: {}", user.getUsername().value(), userId.id());
            return true;
        } else {
            logger.warn("Failed to update name: User not found with ID: {}", userId.id());
            return false;
        }
    }

    @Override
    public boolean updateUserEmail(UserId userId, EmailAddress email) {
        logger.debug("Updating email for user with ID: {}", userId.id());
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            user.setEmail(email);
            userRepository.save(user);
            logger.info("Updated email for user: {}, ID: {}", user.getUsername().value(), userId.id());
            return true;
        } else {
            logger.warn("Failed to update email: User not found with ID: {}", userId.id());
            return false;
        }
    }

    @Override
    public boolean updateUserPhoneNumber(UserId userId, PhoneNumber phoneNumber) {
        logger.debug("Updating phone number for user with ID: {}", userId.id());
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);
            logger.info("Updated phone number for user: {}, ID: {}", user.getUsername().value(), userId.id());
            return true;
        } else {
            logger.warn("Failed to update phone number: User not found with ID: {}", userId.id());
            return false;
        }
    }

    @Override
    public boolean updateUserPassword(UserId userId, String currentPassword, String newPassword) {
        logger.debug("Attempting to update password for user with ID: {}", userId.id());
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();

            // Verify current password
            if (!user.getPassword().matches(currentPassword, passwordHasher)) {
                logger.warn("Password update failed: Current password is incorrect for user: {}", user.getUsername().value());
                return false; // Current password is incorrect
            }

            // Update password
            HashedPassword newHashedPassword = HashedPassword.fromPlainText(newPassword, passwordHasher);
            user.setPassword(newHashedPassword);
            userRepository.save(user);
            logger.info("Password updated successfully for user: {}, ID: {}", user.getUsername().value(), userId.id());
            return true;
        } else {
            logger.warn("Failed to update password: User not found with ID: {}", userId.id());
            return false;
        }
    }
}
