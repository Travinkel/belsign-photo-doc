package com.belman.business.domain.user;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a user.
 */
public record UserId(String id) {

    /**
     * Creates a new UserId with the specified ID.
     *
     * @param id the ID value
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public UserId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or blank");
        }
    }

    /**
     * Creates a new UserId with a random UUID.
     *
     * @return a new UserId with a random UUID
     */
    public static UserId newId() {
        return new UserId(UUID.randomUUID().toString());
    }
}