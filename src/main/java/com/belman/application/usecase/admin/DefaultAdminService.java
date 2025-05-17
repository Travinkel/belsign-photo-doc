package com.belman.application.usecase.admin;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the AdminService interface.
 * This service provides administrative functionality for managing users.
 */
public class DefaultAdminService implements AdminService {

    private final UserRepository userRepository;

    /**
     * Creates a new DefaultAdminService with the specified UserRepository.
     *
     * @param userRepository the user repository
     */
    public DefaultAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            // Create the user objects
            Username usernameObj = new Username(username);
            HashedPassword passwordObj = HashedPassword.fromPlainText(password, null);
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

            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteUser(UserId userId) {
        try {
            return userRepository.deleteById(userId);
        } catch (Exception e) {
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

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
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

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
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
                HashedPassword passwordObj = HashedPassword.fromPlainText(newPassword, null);

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

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
