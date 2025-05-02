package com.belman.domain.repositories;

import com.belman.domain.aggregates.User;
import com.belman.domain.aggregates.User.Role;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.UserId;
import com.belman.domain.valueobjects.Username;

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
    Optional<User> findByUsername(Username username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(EmailAddress email);

    /**
     * Finds a user by ID.
     *
     * @param id the user ID to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(UserId id);

    /**
     * Finds all users.
     *
     * @return a list of all users
     */
    List<User> findAll();

    /**
     * Finds all users with the specified role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRole(Role role);

    /**
     * Saves a user (creates or updates).
     *
     * @param user the user to save
     */
    void save(User user);

    /**
     * Deletes a user.
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     */
    boolean delete(UserId id);
}
