package com.belman.domain.user;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.core.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User business object.
 */
public interface UserRepository extends Repository<UserBusiness, UserId> {
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
     * Finds all users with the specified role.
     *
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<UserBusiness> findByRole(UserRole role);
}