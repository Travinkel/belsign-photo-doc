package com.belman.domain.photo;

import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;

/**
 * Value object representing a reference to a user.
 * This is used to reference users from other contexts without creating a direct dependency
 * on the User aggregate. This follows the Domain-Driven Design principle of using references
 * across bounded contexts.
 */
public record UserReference(UserId id, Username username) {

    /**
     * Creates a new UserReference with the specified ID and username.
     *
     * @param id       the user ID
     * @param username the username
     * @throws NullPointerException if id or username is null
     */
    public UserReference {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
    }
}