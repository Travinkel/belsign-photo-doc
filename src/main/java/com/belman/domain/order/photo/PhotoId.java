package com.belman.domain.order.photo;

import java.util.UUID;

/**
 * Value object representing a unique identifier for a photo document.
 * This is specific to the photo bounded context.
 */
public record PhotoId(String id) {

    /**
     * Creates a new PhotoId with the specified ID.
     *
     * @param id the ID value
     * @throws IllegalArgumentException if the ID is null or empty
     */
    public PhotoId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Photo ID must not be null or blank");
        }
    }

    /**
     * Creates a new PhotoId with a random UUID.
     *
     * @return a new PhotoId with a random UUID
     */
    public static PhotoId newId() {
        return new PhotoId(UUID.randomUUID().toString());
    }
}