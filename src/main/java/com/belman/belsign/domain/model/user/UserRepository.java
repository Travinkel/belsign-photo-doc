package com.belman.belsign.domain.model.user;

import com.belman.belsign.domain.model.user.User;


import java.util.Optional;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository {
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(EmailAddress email);
    void save(User user);
}
