package com.belman.domain.user;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a user.
 */
public record UserId(String id) {

    /**
     * Creates a new UserId with the specified ID.
     * The ID is normalized to lowercase to ensure consistent comparison across all storage backends.
     *
     * @param id the ID value
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public UserId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID must not be null or blank");
        }
        // Normalize UUID to lowercase for consistent comparison
        id = id.toLowerCase();
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
