package com.belman.application.usecase.admin;

import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;

import java.util.List;

public interface AdminService {
    /**
     * Gets all users in the system.
     *
     * @return a list of all users
     */
    List<UserBusiness> getAllUsers();

    /**
     * Creates a new user with the specified details.
     *
     * @param username  the username
     * @param password  the password
     * @param firstName the first name
     * @param lastName  the last name
     * @param email     the email address
     * @param roles     the roles to assign to the user
     * @return the created user
     */
    UserBusiness createUser(String username, String password, String firstName, String lastName, String email,
                            UserRole[] roles);

    /**
     * Deletes a user with the specified ID.
     *
     * @param userId the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     */
    boolean deleteUser(UserId userId);

    /**
     * Assigns a role to a user.
     *
     * @param userId the ID of the user
     * @param role   the role to assign
     * @return true if the role was assigned, false if the user was not found
     */
    boolean assignRole(UserId userId, UserRole role);

    /**
     * Removes a role from a user.
     *
     * @param userId the ID of the user
     * @param role   the role to remove
     * @return true if the role was removed, false if the user was not found
     */
    boolean removeRole(UserId userId, UserRole role);

    /**
     * Resets a user's password.
     *
     * @param userId      the ID of the user
     * @param newPassword the new password
     * @return true if the password was reset, false if the user was not found
     */
    boolean resetPassword(UserId userId, String newPassword);
}
