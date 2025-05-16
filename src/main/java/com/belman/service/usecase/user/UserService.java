package com.belman.service.usecase.user;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Service for user management.
 * Provides methods for creating, updating, and retrieving users.
 */
public interface UserService {
    /**
     * Gets a user by ID.
     *
     * @param userId the ID of the user to get
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserBusiness> getUserById(UserId userId);

    /**
     * Gets a user by username.
     *
     * @param username the username of the user to get
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserBusiness> getUserByUsername(String username);

    /**
     * Gets all users.
     *
     * @return a list of all users
     */
    List<UserBusiness> getAllUsers();

    /**
     * Gets all users with the specified role.
     *
     * @param role the role to filter by
     * @return a list of users with the specified role
     */
    List<UserBusiness> getUsersByRole(UserRole role);

    /**
     * Updates a user's name.
     *
     * @param userId the ID of the user to update
     * @param name   the new name
     * @return true if the user was updated, false if the user was not found
     */
    boolean updateUserName(UserId userId, PersonName name);

    /**
     * Updates a user's email address.
     *
     * @param userId the ID of the user to update
     * @param email  the new email address
     * @return true if the user was updated, false if the user was not found
     */
    boolean updateUserEmail(UserId userId, EmailAddress email);

    /**
     * Updates a user's phone number.
     *
     * @param userId      the ID of the user to update
     * @param phoneNumber the new phone number
     * @return true if the user was updated, false if the user was not found
     */
    boolean updateUserPhoneNumber(UserId userId, PhoneNumber phoneNumber);

    /**
     * Updates a user's password.
     *
     * @param userId          the ID of the user to update
     * @param currentPassword the current password
     * @param newPassword     the new password
     * @return true if the password was updated, false if the user was not found or the current password is incorrect
     */
    boolean updateUserPassword(UserId userId, String currentPassword, String newPassword);
}