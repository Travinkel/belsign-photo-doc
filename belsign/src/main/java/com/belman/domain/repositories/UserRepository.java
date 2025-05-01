package com.belman.domain.repositories;



import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.Username;

import java.util.Optional;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository {
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(EmailAddress email);
    void save(User user);
}
