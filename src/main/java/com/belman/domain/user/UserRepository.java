package com.belman.domain.user;

import com.belman.domain.common.EmailAddress;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository {
    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserBusiness> findByUsername(Username username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserBusiness> findByEmail(EmailAddress email);

    /**
     * Finds a user by ID.
     *
     * @param id the user ID to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserBusiness> findById(UserId id);

    /**
     * Finds all users.
     *
     * @return a list of all users
     */
    List<UserBusiness> findAll();

    /**
     * Finds all users with the specified role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<UserBusiness> findByRole(UserRole role);

    /**
     * Saves a user (creates or updates).
     *
     * @param user the user to save
     */
    void save(UserBusiness user);

    /**
     * Deletes a user.
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     */
    boolean delete(UserId id);
}