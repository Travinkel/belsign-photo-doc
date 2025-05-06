package com.belman.domain.user;

import com.belman.domain.common.EmailAddress;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User aggregate.
 * This follows the Repository pattern from Domain-Driven Design.
 */
public interface UserRepository {
    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserAggregate> findByUsername(Username username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserAggregate> findByEmail(EmailAddress email);

    /**
     * Finds a user by their ID.
     *
     * @param id the ID to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserAggregate> findById(UserId id);

    /**
     * Saves a user.
     * If the user already exists, it will be updated.
     * If the user does not exist, it will be created.
     *
     * @param user the user to save
     */
    void save(UserAggregate user);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false if the user was not found
     */
    boolean delete(UserId id);

    /**
     * Gets all users.
     *
     * @return a list of all users
     */
    List<UserAggregate> findAll();

    /**
     * Gets all users with the specified role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<UserAggregate> findByRole(UserRole role);

    /**
     * Finds a user by their PIN code.
     *
     * @param pinCode the PIN code to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserAggregate> findByPinCode(String pinCode);
}
