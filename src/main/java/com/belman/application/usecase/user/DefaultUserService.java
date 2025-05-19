package com.belman.application.usecase.user;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
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

    /**
     * Creates a new DefaultUserService with the specified UserRepository and PasswordHasher.
     *
     * @param userRepository the user repository
     * @param passwordHasher the password hasher
     */
    public DefaultUserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Optional<UserBusiness> getUserById(UserId userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<UserBusiness> getUserByUsername(String username) {
        try {
            com.belman.domain.user.Username usernameObj = new com.belman.domain.user.Username(username);
            return userRepository.findByUsername(usernameObj);
        } catch (IllegalArgumentException e) {
            // Invalid username format
            return Optional.empty();
        }
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
    public List<UserBusiness> getUsersByRole(UserRole role) {
        try {
            return userRepository.findByRole(role);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateUserName(UserId userId, PersonName name) {
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            user.setName(name);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUserEmail(UserId userId, EmailAddress email) {
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            user.setEmail(email);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUserPhoneNumber(UserId userId, PhoneNumber phoneNumber) {
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUserPassword(UserId userId, String currentPassword, String newPassword) {
        Optional<UserBusiness> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();

            // Verify current password
            if (!user.getPassword().matches(currentPassword, passwordHasher)) {
                return false; // Current password is incorrect
            }

            // Update password
            HashedPassword newHashedPassword = HashedPassword.fromPlainText(newPassword, passwordHasher);
            user.setPassword(newHashedPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
