package com.belman.domain.user;

import com.belman.domain.common.base.DataObject;

/**
 * Value object representing a username.
 */
public record Username(String value) implements DataObject {

    /**
     * Creates a new Username with the specified value.
     *
     * @param value the username value
     * @throws IllegalArgumentException if the value is null or empty
     */
    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username must not be null or blank");
        }

        if (value.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
    }
}
