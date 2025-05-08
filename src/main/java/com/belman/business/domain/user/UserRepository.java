package com.belman.business.domain.user;

import com.belman.business.domain.common.EmailAddress;

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
    Optional<UserAggregate> findByUsername(Username username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserAggregate> findByEmail(EmailAddress email);

    /**
     * Finds a user by ID.
     *
     * @param id the user ID to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserAggregate> findById(UserId id);

    /**
     * Finds all users.
     *
     * @return a list of all users
     */
    List<UserAggregate> findAll();

    /**
     * Finds all users with the specified role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<UserAggregate> findByRole(UserRole role);

    /**
     * Saves a user (creates or updates).
     *
     * @param user the user to save
     */
    void save(UserAggregate user);

    /**
     * Deletes a user.
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     */
    boolean delete(UserId id);
}